package com.event.reservation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ReservationClockConfiguration {

    @Bean(name = "reservationClock")
    @Profile({"local", "test"})
    Clock reservationClockForLocalAndTest(@Value("${app.reservation.now-override:}") String nowOverride) {
        return resolveClock(nowOverride, true, ZoneId.systemDefault(), Clock.systemDefaultZone());
    }

    @Bean(name = "reservationClock")
    @Profile("!local & !test")
    Clock reservationClockForNonLocalProfiles(@Value("${app.reservation.now-override:}") String nowOverride) {
        return resolveClock(nowOverride, false, ZoneId.systemDefault(), Clock.systemDefaultZone());
    }

    static Clock resolveClock(String nowOverride, boolean overrideEnabled, ZoneId zoneId, Clock fallbackClock) {
        if (!overrideEnabled || nowOverride == null || nowOverride.isBlank()) {
            return fallbackClock;
        }
        try {
            LocalDateTime parsedNowOverride = LocalDateTime.parse(nowOverride);
            return Clock.fixed(parsedNowOverride.atZone(zoneId).toInstant(), zoneId);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "app.reservation.now-override must be ISO-8601 local datetime, for example 2026-01-01T10:01:00",
                ex
            );
        }
    }
}
