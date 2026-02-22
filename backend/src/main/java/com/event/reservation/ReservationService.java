package com.event.reservation;

import com.event.reservation.api.MyPageResponse;
import com.event.reservation.api.AdminSessionResponse;
import com.event.reservation.api.AdminSessionSummaryResponse;
import com.event.reservation.api.ReservationResponse;
import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import com.event.reservation.api.SessionSummaryResponse.SessionSummary;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final int FEW_SEATS_THRESHOLD = 20;
    private static final int MAX_REGULAR_RESERVATIONS = 5;
    private static final String KEYNOTE_SESSION = "keynote";
    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Map<String, Integer> TRACK_DISPLAY_ORDER_BY_TRACK = Map.of(
        "Keynote", 0,
        "Track A", 1,
        "Track B", 2,
        "Track C", 3
    );

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
        @Value("${app.reservation.event-date:}") String configuredEventDate,
        @Qualifier("reservationClock") Clock reservationClock
    ) {
        this(keynoteCapacity, regularSessionCapacity, parseEventDate(configuredEventDate), reservationClock);
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
        this.eventDate = eventDate;
        this.clock = clock;
        this.sessionCatalogById = new ConcurrentHashMap<>();
        this.reservationsBySession = new ConcurrentHashMap<>();

        for (SessionDefinition session : createDefaultSessionCatalog(keynoteCapacity, regularSessionCapacity)) {
            sessionCatalogById.put(session.sessionId, session);
            reservationsBySession.put(session.sessionId, ConcurrentHashMap.newKeySet());
        }
    }

    public ReservationResponse listReservations(String guestId) {
        Set<String> guestReservations = reservationsByGuest.getOrDefault(guestId, Set.of());
        Map<String, Integer> orderBySessionId = sessionOrderBySessionId();
        List<String> sortedReservations = guestReservations.stream()
            .sorted(Comparator.comparingInt(sessionId -> orderBySessionId.getOrDefault(sessionId, Integer.MAX_VALUE)))
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
        synchronized (reservationLock) {
            SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
            if (sessionDefinition == null) {
                throw new ReservationRuleViolationException("指定されたセッションは存在しません。");
            }
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
        synchronized (reservationLock) {
            SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
            if (sessionDefinition == null) {
                throw new ReservationRuleViolationException("指定されたセッションは存在しません。");
            }
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
        List<SessionSummary> sessions = sortedSessions().stream()
            .map(sessionDefinition -> new SessionSummary(
                sessionDefinition.sessionId,
                sessionDefinition.title,
                sessionDefinition.startTime.format(START_TIME_FORMATTER),
                sessionDefinition.track,
                sessionDefinition.displayOrder,
                toAvailabilityStatus(remainingSeats(sessionDefinition.sessionId))
            ))
            .toList();
        return new SessionSummaryResponse(sessions);
    }

    public AdminSessionSummaryResponse listAdminSessions() {
        List<AdminSessionResponse> sessions = sortedSessions().stream()
            .map(this::toAdminSessionResponse)
            .toList();
        return new AdminSessionSummaryResponse(sessions);
    }

    public List<ReservationExportRow> listReservationExportRows() {
        synchronized (reservationLock) {
            List<ReservationExportRow> rows = new ArrayList<>();
            for (SessionDefinition sessionDefinition : sortedSessions()) {
                List<String> guestIds = reservationsBySession.getOrDefault(sessionDefinition.sessionId, Set.of()).stream()
                    .sorted()
                    .toList();
                for (String guestId : guestIds) {
                    rows.add(
                        new ReservationExportRow(
                            guestId,
                            sessionDefinition.sessionId,
                            sessionDefinition.title,
                            sessionDefinition.startTime.format(START_TIME_FORMATTER),
                            sessionDefinition.track
                        )
                    );
                }
            }
            return List.copyOf(rows);
        }
    }

    public AdminSessionResponse createSession(String title, String startTime, String track, Integer capacity) {
        validateSessionFields(title, startTime, track, capacity);
        LocalTime parsedStartTime = parseStartTime(startTime);
        int normalizedCapacity = normalizeCapacity(capacity);

        synchronized (reservationLock) {
            String sessionId = nextRegularSessionId();
            String normalizedTrack = track.trim();
            SessionDefinition sessionDefinition = new SessionDefinition(
                sessionId,
                title.trim(),
                normalizedTrack,
                parsedStartTime,
                normalizedCapacity,
                resolveDisplayOrder(normalizedTrack)
            );
            sessionCatalogById.put(sessionId, sessionDefinition);
            reservationsBySession.put(sessionId, ConcurrentHashMap.newKeySet());
            return toAdminSessionResponse(sessionDefinition);
        }
    }

    public AdminSessionResponse updateSession(String sessionId, String title, String startTime, String track, Integer capacity) {
        validateSessionFields(title, startTime, track, capacity);
        LocalTime parsedStartTime = parseStartTime(startTime);
        int normalizedCapacity = normalizeCapacity(capacity);

        synchronized (reservationLock) {
            SessionDefinition currentSession = sessionCatalogById.get(sessionId);
            if (currentSession == null) {
                throw new ReservationRuleViolationException("指定されたセッションは存在しません。");
            }

            int reservedCount = reservedCount(sessionId);
            if (normalizedCapacity < reservedCount) {
                throw new ReservationRuleViolationException("現在の予約数を下回る定員には変更できません。");
            }

            ensureNoTimeConflictForExistingGuests(sessionId, parsedStartTime);
            SessionDefinition updatedSession = new SessionDefinition(
                sessionId,
                title.trim(),
                track.trim(),
                parsedStartTime,
                normalizedCapacity,
                resolveDisplayOrder(track.trim())
            );
            sessionCatalogById.put(sessionId, updatedSession);
            return toAdminSessionResponse(updatedSession);
        }
    }

    private void validateSessionFields(String title, String startTime, String track, Integer capacity) {
        if (title == null || title.isBlank()) {
            throw new ReservationRuleViolationException("タイトルは必須です。");
        }
        if (track == null || track.isBlank()) {
            throw new ReservationRuleViolationException("トラックは必須です。");
        }
        if (startTime == null || startTime.isBlank()) {
            throw new ReservationRuleViolationException("開始時刻は必須です。");
        }
        if (capacity == null || capacity <= 0) {
            throw new ReservationRuleViolationException("定員は1以上で指定してください。");
        }
    }

    private int normalizeCapacity(Integer capacity) {
        Objects.requireNonNull(capacity, "capacity must not be null");
        return capacity;
    }

    private void ensureNoTimeConflictForExistingGuests(String sessionId, LocalTime newStartTime) {
        Set<String> reservedGuests = reservationsBySession.getOrDefault(sessionId, Set.of());
        for (String guestId : reservedGuests) {
            Set<String> guestReservations = reservationsByGuest.getOrDefault(guestId, Set.of());
            for (String reservedSessionId : guestReservations) {
                if (sessionId.equals(reservedSessionId)) {
                    continue;
                }
                SessionDefinition reservedSession = sessionCatalogById.get(reservedSessionId);
                if (reservedSession != null && reservedSession.startTime.equals(newStartTime)) {
                    throw new ReservationRuleViolationException("既存予約との時間帯重複が発生するため開始時刻を変更できません。");
                }
            }
        }
    }

    public boolean sessionExists(String sessionId) {
        return sessionCatalogById.containsKey(sessionId);
    }

    public boolean hasReservation(String guestId, String sessionId) {
        Set<String> guestReservations = reservationsByGuest.getOrDefault(guestId, Set.of());
        return guestReservations.contains(sessionId);
    }

    private void reserveWithCapacityCheck(Set<String> guestReservations, String guestId, String sessionId) {
        Set<String> sessionReservations = reservationsBySession.get(sessionId);
        SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
        if (sessionReservations == null || sessionDefinition == null) {
            throw new IllegalStateException("Reservation bucket or catalog not found for session: " + sessionId);
        }
        if (sessionReservations.size() >= sessionDefinition.capacity) {
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
        int reservedCount = reservedCount(sessionId);
        SessionDefinition sessionDefinition = sessionCatalogById.get(sessionId);
        if (sessionDefinition == null) {
            return 0;
        }
        return Math.max(0, sessionDefinition.capacity - reservedCount);
    }

    private String buildReceptionQrCodePayload(String guestId, List<String> reservations) {
        String reservationPayload = String.join(",", reservations);
        return "event-reservation://checkin?guestId=" + guestId + "&reservations=" + reservationPayload;
    }

    private int reservedCount(String sessionId) {
        return reservationsBySession.getOrDefault(sessionId, Set.of()).size();
    }

    private Map<String, Integer> sessionOrderBySessionId() {
        Map<String, Integer> orderMap = new HashMap<>();
        List<SessionDefinition> orderedSessions = sortedSessions();
        for (int i = 0; i < orderedSessions.size(); i++) {
            orderMap.put(orderedSessions.get(i).sessionId, i);
        }
        return orderMap;
    }

    private List<SessionDefinition> sortedSessions() {
        return sessionCatalogById.values().stream()
            .sorted(Comparator
                .comparing(SessionDefinition::startTime)
                .thenComparing(SessionDefinition::displayOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SessionDefinition::track)
                .thenComparing(SessionDefinition::sessionId))
            .toList();
    }

    private String nextRegularSessionId() {
        int maxSequence = sessionCatalogById.keySet().stream()
            .filter(sessionId -> sessionId.startsWith("session-"))
            .map(sessionId -> sessionId.substring("session-".length()))
            .filter(sequence -> sequence.chars().allMatch(Character::isDigit))
            .mapToInt(Integer::parseInt)
            .max()
            .orElse(0);
        return "session-" + (maxSequence + 1);
    }

    private AdminSessionResponse toAdminSessionResponse(SessionDefinition sessionDefinition) {
        return new AdminSessionResponse(
            sessionDefinition.sessionId,
            sessionDefinition.title,
            sessionDefinition.startTime.format(START_TIME_FORMATTER),
            sessionDefinition.track,
            sessionDefinition.capacity,
            reservedCount(sessionDefinition.sessionId)
        );
    }

    private static LocalDate parseEventDate(String configuredEventDate) {
        if (configuredEventDate == null || configuredEventDate.isBlank()) {
            return LocalDate.now(Clock.systemDefaultZone());
        }
        return LocalDate.parse(configuredEventDate);
    }

    private static LocalTime parseStartTime(String startTime) {
        try {
            return LocalTime.parse(startTime, START_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new ReservationRuleViolationException("開始時刻はHH:mm形式で指定してください。");
        }
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

    private static List<SessionDefinition> createDefaultSessionCatalog(int keynoteCapacity, int regularSessionCapacity) {
        List<SessionDefinition> sessions = new ArrayList<>();
        sessions.add(new SessionDefinition(
            KEYNOTE_SESSION,
            "Opening Keynote",
            "Keynote",
            LocalTime.of(9, 0),
            keynoteCapacity,
            resolveDisplayOrder("Keynote")
        ));

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
                sessions.add(new SessionDefinition(
                    sessionId,
                    title,
                    track,
                    startTime,
                    regularSessionCapacity,
                    resolveDisplayOrder(track)
                ));
                sequence++;
            }
        }

        return sessions;
    }

    private static Integer resolveDisplayOrder(String track) {
        return TRACK_DISPLAY_ORDER_BY_TRACK.get(track);
    }

    private record SessionDefinition(
        String sessionId,
        String title,
        String track,
        LocalTime startTime,
        int capacity,
        Integer displayOrder
    ) {
    }

    public record ReservationExportRow(
        String guestId,
        String sessionId,
        String sessionTitle,
        String startTime,
        String track
    ) {}
}
