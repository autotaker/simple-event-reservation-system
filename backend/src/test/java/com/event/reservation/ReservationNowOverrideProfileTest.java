package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {
        "spring.autoconfigure.exclude="
            + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
            + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
            + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "app.auth.admin-operator-id=test-admin",
        "app.auth.admin-password=test-admin-password",
        "app.reservation.event-date=2099-01-01",
        "app.reservation.now-override=2099-01-01T10:01:00"
    }
)
@ActiveProfiles("test")
class ReservationNowOverrideProfileTest {

    @Autowired
    private ReservationService reservationService;

    @Test
    void reservationDeadlineUsesNowOverrideOnTestProfile() {
        assertThatThrownBy(() -> reservationService.reserveSession("guest-1", "session-1"))
            .isInstanceOf(ReservationRuleViolationException.class)
            .hasMessage("このセッションは開始30分前を過ぎたため予約できません。");
    }
}
