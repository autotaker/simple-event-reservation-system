package com.event.reservation;

import com.event.reservation.api.ReservationResponse;
import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import com.event.reservation.api.SessionSummaryResponse.SessionSummary;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final int FEW_SEATS_THRESHOLD = 20;
    private static final int REGULAR_SESSION_CAPACITY = 200;
    private static final String KEYNOTE_SESSION = "keynote";
    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<SessionDefinition> SESSION_CATALOG = createSessionCatalog();

    private final int keynoteCapacity;
    private final Object keynoteLock = new Object();
    private final Set<String> keynoteReservations = ConcurrentHashMap.newKeySet();

    public ReservationService(@Value("${app.reservation.keynote-capacity:700}") int keynoteCapacity) {
        if (keynoteCapacity <= 0) {
            throw new IllegalArgumentException("keynoteCapacity must be positive");
        }
        this.keynoteCapacity = keynoteCapacity;
    }

    public ReservationResponse listReservations(String guestId) {
        if (keynoteReservations.contains(guestId)) {
            return new ReservationResponse(guestId, List.of(KEYNOTE_SESSION), true);
        }
        return new ReservationResponse(guestId, List.of(), false);
    }

    public ReservationResponse reserveKeynote(String guestId) {
        synchronized (keynoteLock) {
            if (!keynoteReservations.contains(guestId) && keynoteReservations.size() >= keynoteCapacity) {
                throw new KeynoteCapacityExceededException();
            }
            keynoteReservations.add(guestId);
        }
        return new ReservationResponse(guestId, List.of(KEYNOTE_SESSION), true);
    }

    public SessionSummaryResponse listSessions() {
        int keynoteReservedCount = keynoteReservations.size();
        List<SessionSummary> sessions = new ArrayList<>(SESSION_CATALOG.size());
        for (SessionDefinition sessionDefinition : SESSION_CATALOG) {
            int remainingSeats = sessionDefinition.sessionId.equals(KEYNOTE_SESSION)
                ? Math.max(0, keynoteCapacity - keynoteReservedCount)
                : REGULAR_SESSION_CAPACITY;
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
