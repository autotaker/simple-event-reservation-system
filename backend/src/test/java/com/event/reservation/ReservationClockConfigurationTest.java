package com.event.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ReservationClockConfigurationTest {

    @Test
    void resolveClockUsesOverrideWhenEnabled() {
        Clock fallbackClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        Clock resolvedClock = ReservationClockConfiguration.resolveClock(
            "2026-01-01T10:01:00",
            true,
            ZoneOffset.UTC,
            fallbackClock
        );

        assertThat(resolvedClock.instant()).isEqualTo(Instant.parse("2026-01-01T10:01:00Z"));
    }

    @Test
    void resolveClockUsesFallbackWhenOverrideIsBlank() {
        Clock fallbackClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        Clock resolvedClock = ReservationClockConfiguration.resolveClock("", true, ZoneOffset.UTC, fallbackClock);

        assertThat(resolvedClock).isSameAs(fallbackClock);
    }

    @Test
    void resolveClockIgnoresOverrideWhenDisabled() {
        Clock fallbackClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        Clock resolvedClock = ReservationClockConfiguration.resolveClock(
            "2099-01-01T10:01:00",
            false,
            ZoneOffset.UTC,
            fallbackClock
        );

        assertThat(resolvedClock).isSameAs(fallbackClock);
    }

    @Test
    void resolveClockRejectsInvalidFormat() {
        Clock fallbackClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        assertThatThrownBy(() -> ReservationClockConfiguration.resolveClock("2026/01/01 10:01", true, ZoneOffset.UTC, fallbackClock))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("app.reservation.now-override");
    }
}
