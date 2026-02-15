package com.event.reservation;

import com.event.reservation.api.MyPageResponse;
import com.event.reservation.api.ReservationResponse;
import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import com.event.reservation.api.SessionSummaryResponse.SessionSummary;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final int FEW_SEATS_THRESHOLD = 20;
    private static final int MAX_REGULAR_RESERVATIONS = 5;
    private static final String KEYNOTE_SESSION = "keynote";
    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<SessionDefinition> SESSION_CATALOG = createSessionCatalog();

    private final int keynoteCapacity;
    private final int regularSessionCapacity;
    private final LocalDate eventDate;
    private final Clock clock;
    private final Object reservationLock = new Object();
    private final Map<String, SessionDefinition> sessionCatalogById;
    private final Map<String, Set<String>> reservationsBySession;
    private final Map<String, Set<String>> reservationsByGuest = new ConcurrentHashMap<>();

    @Autowired
    public ReservationService(
        @Value("${app.reservation.keynote-capacity:700}") int keynoteCapacity,
        @Value("${app.reservation.regular-capacity:200}") int regularSessionCapacity,
        @Value("${app.reservation.event-date:}") String configuredEventDate
    ) {
        this(keynoteCapacity, regularSessionCapacity, parseEventDate(configuredEventDate), Clock.systemDefaultZone());
    }

    ReservationService(int keynoteCapacity) {
        this(keynoteCapacity, 200, LocalDate.now(Clock.systemDefaultZone()), Clock.systemDefaultZone());
    }

    ReservationService(int keynoteCapacity, int regularSessionCapacity, LocalDate eventDate, Clock clock) {
        if (keynoteCapacity <= 0) {
            throw new IllegalArgumentException("keynoteCapacity must be positive");
        }
        if (regularSessionCapacity <= 0) {
            throw new IllegalArgumentException("regularSessionCapacity must be positive");
        }
        if (eventDate == null) {
            throw new IllegalArgumentException("eventDate must not be null");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }
        this.keynoteCapacity = keynoteCapacity;
        this.regularSessionCapacity = regularSessionCapacity;
        this.eventDate = eventDate;
        this.clock = clock;
        this.sessionCatalogById = toSessionCatalogById();
        this.reservationsBySession = createReservationBuckets();
    }

    public ReservationResponse listReservations(String guestId) {
        Set<String> guestReservations = reservationsByGuest.getOrDefault(guestId, Set.of());
        List<String> sortedReservations = guestReservations.stream()
            .sorted(Comparator.comparingInt(this::catalogOrder))
            .toList();
        if (!sortedReservations.isEmpty()) {
            return new ReservationResponse(guestId, sortedReservations, sortedReservations.contains(KEYNOTE_SESSION));
        }
        return new ReservationResponse(guestId, List.of(), false);
    }

    public MyPageResponse getMyPage(String guestId) {
        ReservationResponse reservationResponse = listReservations(guestId);
        return new MyPageResponse(
            guestId,
            reservationResponse.reservations(),
            buildReceptionQrCodePayload(guestId, reservationResponse.reservations())
        );
    }

    public ReservationResponse reserveKeynote(String guestId) {
        return reserveSession(guestId, KEYNOTE_SESSION);
    }

    public ReservationResponse reserveSession(String guestId, String sessionId) {
        SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
        if (sessionDefinition == null) {
            throw new ReservationRuleViolationException("指定されたセッションは存在しません。");
        }
        synchronized (reservationLock) {
            Set<String> guestReservations = reservationsByGuest.computeIfAbsent(guestId, key -> ConcurrentHashMap.newKeySet());
            if (guestReservations.contains(sessionId)) {
                return listReservations(guestId);
            }
            if (isDeadlineExceeded(sessionDefinition.startTime)) {
                throw new ReservationRuleViolationException("このセッションは開始30分前を過ぎたため予約できません。");
            }
            String conflictingReservationId = findTimeSlotConflictReservationId(guestReservations, sessionDefinition);
            if (conflictingReservationId == null) {
                ensureRegularSessionLimit(guestReservations, sessionDefinition.sessionId);
            }
            reserveWithCapacityCheck(guestReservations, guestId, sessionDefinition.sessionId);
            if (conflictingReservationId != null) {
                removeReservation(guestReservations, guestId, conflictingReservationId);
            }
        }
        return listReservations(guestId);
    }

    public ReservationResponse cancelSessionReservation(String guestId, String sessionId) {
        SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
        if (sessionDefinition == null) {
            throw new ReservationRuleViolationException("指定されたセッションは存在しません。");
        }

        synchronized (reservationLock) {
            Set<String> guestReservations = reservationsByGuest.get(guestId);
            if (guestReservations == null || !guestReservations.contains(sessionId)) {
                return listReservations(guestId);
            }
            if (isDeadlineExceeded(sessionDefinition.startTime)) {
                throw new ReservationRuleViolationException("このセッションは開始30分前を過ぎたためキャンセルできません。");
            }
            removeReservation(guestReservations, guestId, sessionId);
        }

        return listReservations(guestId);
    }

    public SessionSummaryResponse listSessions() {
        List<SessionSummary> sessions = new ArrayList<>(SESSION_CATALOG.size());
        for (SessionDefinition sessionDefinition : SESSION_CATALOG) {
            int remainingSeats = remainingSeats(sessionDefinition.sessionId);
            sessions.add(new SessionSummary(
                sessionDefinition.sessionId,
                sessionDefinition.title,
                sessionDefinition.startTime.format(START_TIME_FORMATTER),
                sessionDefinition.track,
                toAvailabilityStatus(remainingSeats)
            ));
        }
        return new SessionSummaryResponse(sessions);
    }

    public boolean sessionExists(String sessionId) {
        return sessionCatalogById.containsKey(sessionId);
    }

    private void reserveWithCapacityCheck(Set<String> guestReservations, String guestId, String sessionId) {
        Set<String> sessionReservations = reservationsBySession.get(sessionId);
        if (sessionReservations == null) {
            throw new IllegalStateException("Reservation bucket not found for session: " + sessionId);
        }
        if (sessionReservations.size() >= capacityFor(sessionId)) {
            throw new SessionCapacityExceededException(sessionId);
        }
        sessionReservations.add(guestId);
        guestReservations.add(sessionId);
    }

    private void removeReservation(Set<String> guestReservations, String guestId, String sessionId) {
        Set<String> sessionReservations = reservationsBySession.get(sessionId);
        if (sessionReservations == null) {
            throw new IllegalStateException("Reservation bucket not found for session: " + sessionId);
        }
        sessionReservations.remove(guestId);
        guestReservations.remove(sessionId);
        if (guestReservations.isEmpty()) {
            reservationsByGuest.remove(guestId);
        }
    }

    private String findTimeSlotConflictReservationId(Set<String> guestReservations, SessionDefinition reservationTarget) {
        return guestReservations.stream()
            .map(sessionCatalogById::get)
            .filter(existingSession ->
                existingSession != null && existingSession.startTime.equals(reservationTarget.startTime))
            .map(SessionDefinition::sessionId)
            .findFirst()
            .orElse(null);
    }

    private void ensureRegularSessionLimit(Set<String> guestReservations, String sessionId) {
        if (KEYNOTE_SESSION.equals(sessionId)) {
            return;
        }
        long regularReservationCount = guestReservations.stream()
            .filter(guestReservation -> !KEYNOTE_SESSION.equals(guestReservation))
            .count();
        if (regularReservationCount >= MAX_REGULAR_RESERVATIONS) {
            throw new ReservationRuleViolationException("通常セッションは最大5件までしか予約できません。");
        }
    }

    private boolean isDeadlineExceeded(LocalTime startTime) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime deadline = LocalDateTime.of(eventDate, startTime.minusMinutes(30));
        return now.isAfter(deadline);
    }

    private int remainingSeats(String sessionId) {
        int reservedCount = reservationsBySession.getOrDefault(sessionId, Set.of()).size();
        return Math.max(0, capacityFor(sessionId) - reservedCount);
    }

    private int capacityFor(String sessionId) {
        return KEYNOTE_SESSION.equals(sessionId) ? keynoteCapacity : regularSessionCapacity;
    }

    private int catalogOrder(String sessionId) {
        for (int i = 0; i < SESSION_CATALOG.size(); i++) {
            if (SESSION_CATALOG.get(i).sessionId.equals(sessionId)) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    private String buildReceptionQrCodePayload(String guestId, List<String> reservations) {
        String reservationPayload = String.join(",", reservations);
        return "event-reservation://checkin?guestId=" + guestId + "&reservations=" + reservationPayload;
    }

    private Map<String, SessionDefinition> toSessionCatalogById() {
        Map<String, SessionDefinition> catalogById = new HashMap<>();
        for (SessionDefinition sessionDefinition : SESSION_CATALOG) {
            catalogById.put(sessionDefinition.sessionId, sessionDefinition);
        }
        return Map.copyOf(catalogById);
    }

    private Map<String, Set<String>> createReservationBuckets() {
        Map<String, Set<String>> buckets = new ConcurrentHashMap<>();
        for (SessionDefinition sessionDefinition : SESSION_CATALOG) {
            buckets.put(sessionDefinition.sessionId, ConcurrentHashMap.newKeySet());
        }
        return buckets;
    }

    private static LocalDate parseEventDate(String configuredEventDate) {
        if (configuredEventDate == null || configuredEventDate.isBlank()) {
            return LocalDate.now(Clock.systemDefaultZone());
        }
        return LocalDate.parse(configuredEventDate);
    }

    private static SessionAvailabilityStatus toAvailabilityStatus(int remainingSeats) {
        if (remainingSeats <= 0) {
            return SessionAvailabilityStatus.FULL;
        }
        if (remainingSeats < FEW_SEATS_THRESHOLD) {
            return SessionAvailabilityStatus.FEW_LEFT;
        }
        return SessionAvailabilityStatus.OPEN;
    }

    private static List<SessionDefinition> createSessionCatalog() {
        List<SessionDefinition> sessions = new ArrayList<>();
        sessions.add(new SessionDefinition(KEYNOTE_SESSION, "Opening Keynote", "Keynote", LocalTime.of(9, 0)));

        LocalTime[] regularStartTimes = {
            LocalTime.of(10, 30),
            LocalTime.of(11, 30),
            LocalTime.of(13, 30),
            LocalTime.of(14, 30),
            LocalTime.of(15, 30)
        };
        String[] tracks = {"Track A", "Track B", "Track C"};

        int sequence = 1;
        for (LocalTime startTime : regularStartTimes) {
            for (String track : tracks) {
                String sessionId = "session-" + sequence;
                String title = "Session " + sequence;
                sessions.add(new SessionDefinition(sessionId, title, track, startTime));
                sequence++;
            }
        }

        return List.copyOf(sessions);
    }

    private record SessionDefinition(String sessionId, String title, String track, LocalTime startTime) {
    }
}
