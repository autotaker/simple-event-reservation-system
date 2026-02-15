package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
    void reserveSessionReplacesReservationForSameStartTime() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T09:00:00Z"));

        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-1", "session-2");

        assertThat(reservationService.listReservations("guest-1").reservations()).containsExactly("session-2");
    }

    @Test
    void reserveSessionRejectsMoreThanFiveRegularSessions() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));

        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-1", "session-4");
        reservationService.reserveSession("guest-1", "session-7");
        reservationService.reserveSession("guest-1", "session-10");
        reservationService.reserveSession("guest-1", "session-13");
        reservationService.reserveSession("guest-1", "session-14");

        assertThat(reservationService.listReservations("guest-1").reservations())
            .containsExactly("session-1", "session-4", "session-7", "session-10", "session-14");
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

    @Test
    void cancelSessionReservationRemovesReservationBeforeDeadline() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));
        reservationService.reserveSession("guest-1", "session-1");

        reservationService.cancelSessionReservation("guest-1", "session-1");

        assertThat(reservationService.listReservations("guest-1").reservations()).isEmpty();
    }

    @Test
    void cancelSessionReservationRejectsWhenDeadlineExceeded() {
        MutableClock mutableClock = new MutableClock("2026-01-01T09:00:00Z");
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, mutableClock);
        reservationService.reserveSession("guest-1", "session-1");
        mutableClock.setInstant("2026-01-01T10:01:00Z");

        assertThatThrownBy(() -> reservationService.cancelSessionReservation("guest-1", "session-1"))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("このセッションは開始30分前を過ぎたためキャンセルできません。");
    }

    @Test
    void createSessionAddsNewSessionToParticipantList() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));

        reservationService.createSession("New Session", "16:30", "Track D", 30);

        assertThat(reservationService.listSessions().sessions())
            .extracting(SessionSummaryResponse.SessionSummary::title)
            .contains("New Session");
    }

    @Test
    void updateSessionRejectsCapacityBelowCurrentReservations() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));
        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-2", "session-1");

        assertThatThrownBy(() -> reservationService.updateSession("session-1", "Session 1", "10:30", "Track A", 1))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("現在の予約数を下回る定員には変更できません。");
    }

    @Test
    void updateSessionRejectsTimeChangeWhenExistingReservationsWouldConflict() {
        ReservationService reservationService = new ReservationService(200, 200, EVENT_DATE, fixedClockAt("2026-01-01T08:00:00Z"));
        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-1", "session-4");

        assertThatThrownBy(() -> reservationService.updateSession("session-4", "Session 4", "10:30", "Track A", 200))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("既存予約との時間帯重複が発生するため開始時刻を変更できません。");
    }

    private Clock fixedClockAt(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(String isoInstant) {
            this.instant = Instant.parse(isoInstant);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void setInstant(String isoInstant) {
            this.instant = Instant.parse(isoInstant);
        }
    }
}
