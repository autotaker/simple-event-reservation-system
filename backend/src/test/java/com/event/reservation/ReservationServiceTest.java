package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {

    @Test
    void listSessionsReturnsFewLeftForKeynoteWhenRemainingSeatsBelowThreshold() {
        ReservationService reservationService = new ReservationService(20);
        for (int i = 0; i < 19; i++) {
            reservationService.reserveKeynote("guest-" + i);
        }

        SessionSummaryResponse response = reservationService.listSessions();

        assertThat(response.sessions()).hasSize(16);
        assertThat(response.sessions().getFirst().availabilityStatus()).isEqualTo(SessionAvailabilityStatus.FEW_LEFT);
    }

    @Test
    void listSessionsReturnsFullForKeynoteWhenCapacityReached() {
        ReservationService reservationService = new ReservationService(1);
        reservationService.reserveKeynote("guest-1");

        SessionSummaryResponse response = reservationService.listSessions();

        assertThat(response.sessions().getFirst().availabilityStatus()).isEqualTo(SessionAvailabilityStatus.FULL);
    }
}
