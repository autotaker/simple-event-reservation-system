package com.event.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class GuestSessionServiceTest {

    @Test
    void issuedTokenCanBeResolvedBeforeExpiry() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-15T00:00:00Z"));
        GuestSessionService service = new GuestSessionService(clock, Duration.ofMinutes(30), 10);

        GuestSession session = service.issueSession();

        assertThat(service.resolveGuestId(session.token())).contains(session.guestId());
    }

    @Test
    void expiredTokenIsRejected() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-15T00:00:00Z"));
        GuestSessionService service = new GuestSessionService(clock, Duration.ofMinutes(5), 10);

        GuestSession session = service.issueSession();
        clock.advance(Duration.ofMinutes(5));

        assertThat(service.resolveGuestId(session.token())).isEmpty();
    }

    @Test
    void oldestSessionIsEvictedWhenSessionCountHitsLimit() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-15T00:00:00Z"));
        GuestSessionService service = new GuestSessionService(clock, Duration.ofHours(1), 2);

        GuestSession first = service.issueSession();
        clock.advance(Duration.ofSeconds(1));
        GuestSession second = service.issueSession();
        clock.advance(Duration.ofSeconds(1));
        GuestSession third = service.issueSession();

        assertThat(service.resolveGuestId(first.token())).isEmpty();
        assertThat(service.resolveGuestId(second.token())).contains(second.guestId());
        assertThat(service.resolveGuestId(third.token())).contains(third.guestId());
    }

    private static final class MutableClock extends Clock {

        private Instant currentInstant;

        private MutableClock(Instant initialInstant) {
            this.currentInstant = initialInstant;
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
            return currentInstant;
        }

        private void advance(Duration duration) {
            currentInstant = currentInstant.plus(duration);
        }
    }
}
