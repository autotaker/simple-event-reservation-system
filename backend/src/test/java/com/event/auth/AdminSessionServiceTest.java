package com.event.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class AdminSessionServiceTest {

    @Test
    void resolveReturnsExpiredAfterTtlElapsed() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-22T00:00:00Z"), ZoneId.of("UTC"));
        AdminSessionService service = new AdminSessionService(
            "operator",
            "password",
            clock,
            Duration.ofSeconds(1),
            10
        );

        AdminSession session = service.login("operator", "password").orElseThrow();
        clock.advance(Duration.ofSeconds(2));

        AdminTokenValidationResult result = service.resolve(session.token());
        assertThat(result.state()).isEqualTo(AdminTokenState.EXPIRED);
    }

    @Test
    void constructorRejectsBlankCredentials() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-22T00:00:00Z"), ZoneId.of("UTC"));

        assertThatThrownBy(() -> new AdminSessionService(" ", "password", clock, Duration.ofMinutes(30), 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("adminOperatorId must not be blank");

        assertThatThrownBy(() -> new AdminSessionService("operator", "", clock, Duration.ofMinutes(30), 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("adminPassword must not be blank");
    }

    @Test
    void revokeEvictsOldestWhenRevokedSessionsReachCapacity() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-22T00:00:00Z"), ZoneId.of("UTC"));
        AdminSessionService service = new AdminSessionService(
            "operator",
            "password",
            clock,
            Duration.ofMinutes(30),
            1
        );

        AdminSession first = service.login("operator", "password").orElseThrow();
        service.revoke(first.token());

        clock.advance(Duration.ofSeconds(1));
        AdminSession second = service.login("operator", "password").orElseThrow();
        service.revoke(second.token());

        assertThat(service.resolve(first.token()).state()).isEqualTo(AdminTokenState.UNAUTHORIZED);
        assertThat(service.resolve(second.token()).state()).isEqualTo(AdminTokenState.REVOKED);
    }

    private static final class MutableClock extends Clock {

        private Instant currentInstant;
        private final ZoneId zoneId;

        private MutableClock(Instant currentInstant, ZoneId zoneId) {
            this.currentInstant = currentInstant;
            this.zoneId = zoneId;
        }

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(currentInstant, zone);
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }

        private void advance(Duration duration) {
            currentInstant = currentInstant.plus(duration);
        }
    }
}
