package com.event.reservation;

import com.event.reservation.api.ReservationResponse;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final String KEYNOTE_SESSION = "keynote";

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
}
