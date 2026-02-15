package com.event.checkin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.event.reservation.ReservationService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckInServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-01-01T09:00:00Z"), ZoneOffset.UTC);
    private ReservationService reservationService;
    private CheckInService checkInService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(200, 200, "2099-01-01", FIXED_CLOCK);
        checkInService = new CheckInService(reservationService);
    }

    @Test
    void checkInEventRecordsAndMarksDuplicateOnSecondScan() {
        String payload = checkInPayload("guest-1", "keynote");

        CheckInService.CheckInResult first = checkInService.checkInEvent(payload);
        CheckInService.CheckInResult second = checkInService.checkInEvent(payload);

        assertThat(first.duplicate()).isFalse();
        assertThat(first.checkInType()).isEqualTo(CheckInType.EVENT);
        assertThat(first.guestId()).isEqualTo("guest-1");
        assertThat(second.duplicate()).isTrue();
        assertThat(second.checkedInAt()).isEqualTo(first.checkedInAt());
    }

    @Test
    void checkInSessionRecordsAndMarksDuplicateOnSecondScan() {
        reservationService.reserveSession("guest-1", "session-1");
        String payload = checkInPayload("guest-1", "session-1");

        CheckInService.CheckInResult first = checkInService.checkInSession("session-1", payload);
        CheckInService.CheckInResult second = checkInService.checkInSession("session-1", payload);

        assertThat(first.duplicate()).isFalse();
        assertThat(first.checkInType()).isEqualTo(CheckInType.SESSION);
        assertThat(first.sessionId()).isEqualTo("session-1");
        assertThat(second.duplicate()).isTrue();
        assertThat(second.checkedInAt()).isEqualTo(first.checkedInAt());
    }

    @Test
    void checkInSessionRejectsWhenGuestDoesNotHaveCurrentReservation() {
        reservationService.reserveSession("guest-1", "session-1");
        String payload = checkInPayload("guest-1", "session-1");
        reservationService.cancelSessionReservation("guest-1", "session-1");

        assertThatThrownBy(() -> checkInService.checkInSession("session-1", payload))
            .isInstanceOf(CheckInRuleViolationException.class)
            .hasMessage("対象ゲストはこのセッションを現在予約していません。");
    }

    @Test
    void checkInSessionRejectsWhenQrReservationDoesNotContainTargetSession() {
        reservationService.reserveSession("guest-1", "session-1");
        String payload = checkInPayload("guest-1", "session-2");

        assertThatThrownBy(() -> checkInService.checkInSession("session-1", payload))
            .isInstanceOf(CheckInRuleViolationException.class)
            .hasMessage("このQRコードでは対象セッションをチェックインできません。");
    }

    @Test
    void checkInEventRejectsMalformedPayload() {
        assertThatThrownBy(() -> checkInService.checkInEvent("not-a-qr"))
            .isInstanceOf(CheckInRuleViolationException.class)
            .hasMessage("QRコードの形式が不正です。");
    }

    @Test
    void listCheckInsByGuestIdReturnsOnlyTargetGuestRecords() {
        reservationService.reserveSession("guest-1", "session-1");
        reservationService.reserveSession("guest-2", "session-2");
        checkInService.checkInEvent(checkInPayload("guest-1", "keynote"));
        checkInService.checkInSession("session-1", checkInPayload("guest-1", "session-1"));
        checkInService.checkInSession("session-2", checkInPayload("guest-2", "session-2"));

        assertThat(checkInService.listCheckInsByGuestId("guest-1"))
            .hasSize(2)
            .allMatch(item -> item.guestId().equals("guest-1"));
    }

    private String checkInPayload(String guestId, String reservations) {
        return "event-reservation://checkin?guestId=" + guestId + "&reservations=" + reservations;
    }
}
