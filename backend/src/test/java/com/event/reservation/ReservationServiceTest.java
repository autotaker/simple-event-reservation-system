package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {

    private static final LocalDate EVENT_DATE = LocalDate.of(2026, 1, 1);

    @Test
    void listSessionsReturnsFewLeftForKeynoteWhenRemainingSeatsBelowThreshold() {
        ReservationService reservationService = new ReservationService(20, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));
        for (int i = 0; i < 19; i++) {
            reservationService.reserveKeynote("guest-" + i);
        }

        SessionSummaryResponse response = reservationService.listSessions();

        assertThat(response.sessions()).hasSize(16);
        assertThat(response.sessions().getFirst().availabilityStatus()).isEqualTo(SessionAvailabilityStatus.FEW_LEFT);
    }

    @Test
    void listSessionsReturnsFullForKeynoteWhenCapacityReached() {
        ReservationService reservationService = new ReservationService(1, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));
        reservationService.reserveKeynote("guest-1");

        SessionSummaryResponse response = reservationService.listSessions();

        assertThat(response.sessions().getFirst().availabilityStatus()).isEqualTo(SessionAvailabilityStatus.FULL);
    }

    @Test
    void reserveSessionRejectsDuplicateReservationForSameStartTime() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T09:00:00Z"));

        reservationService.reserveSession("guest-1", "session-1");

        assertThatThrownBy(() -> reservationService.reserveSession("guest-1", "session-2"))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("同じ時間帯のセッションは1つまでしか予約できません。");
    }

    @Test
    void reserveSessionRejectsMoreThanFiveRegularSessions() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));

        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-1", "session-4");
        reservationService.reserveSession("guest-1", "session-7");
        reservationService.reserveSession("guest-1", "session-10");
        reservationService.reserveSession("guest-1", "session-13");

        assertThatThrownBy(() -> reservationService.reserveSession("guest-1", "session-14"))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("通常セッションは最大5件までしか予約できません。");
    }

    @Test
    void reserveSessionRejectsWhenDeadlineExceeded() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T10:01:00Z"));

        assertThatThrownBy(() -> reservationService.reserveSession("guest-1", "session-1"))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("このセッションは開始30分前を過ぎたため予約できません。");
    }

    @Test
    void reserveSessionRejectsWhenSessionCapacityReached() {
        ReservationService reservationService = new ReservationService(1, 1, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));

        reservationService.reserveSession("guest-1", "session-1");

        assertThatThrownBy(() -> reservationService.reserveSession("guest-2", "session-1"))
            .isInstanceOf(SessionCapacityExceededException.class)
            .hasMessage("セッションは満席です。");
    }

    private Clock fixedClockAt(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }
}
