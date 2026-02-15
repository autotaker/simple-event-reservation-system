package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.event.reservation.api.ReservationResponse;
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
        "app.reservation.event-date=2999-01-01",
        "app.reservation.now-override=2999-01-01T10:01:00"
    }
)
@ActiveProfiles("production")
class ReservationNowOverrideDisabledProfileTest {

    @Autowired
    private ReservationService reservationService;

    @Test
    void nonLocalProfileIgnoresNowOverride() {
        ReservationResponse response = reservationService.reserveSession("guest-1", "session-1");

        assertThat(response.reservations()).containsExactly("session-1");
    }
}
