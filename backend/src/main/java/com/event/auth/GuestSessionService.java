package com.event.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GuestSessionService {

    private static final Duration DEFAULT_SESSION_TTL = Duration.ofHours(1);
    private static final int DEFAULT_MAX_SESSIONS = 10_000;

    private final Map<String, GuestSessionEntry> guestSessions = new ConcurrentHashMap<>();
    private final Clock clock;
    private final Duration sessionTtl;
    private final int maxSessions;

    public GuestSessionService() {
        this(Clock.systemUTC(), DEFAULT_SESSION_TTL, DEFAULT_MAX_SESSIONS);
    }

    GuestSessionService(Clock clock, Duration sessionTtl, int maxSessions) {
        if (sessionTtl.isZero() || sessionTtl.isNegative()) {
            throw new IllegalArgumentException("sessionTtl must be positive");
        }
        if (maxSessions <= 0) {
            throw new IllegalArgumentException("maxSessions must be positive");
        }
        this.clock = clock;
        this.sessionTtl = sessionTtl;
        this.maxSessions = maxSessions;
    }

    public GuestSession issueSession() {
        Instant now = clock.instant();
        evictExpiredSessions(now);
        if (guestSessions.size() >= maxSessions) {
            evictOldestSession();
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        String guestId = "guest-" + token.substring(0, 8);
        Instant expiresAt = now.plus(sessionTtl);
        guestSessions.put(token, new GuestSessionEntry(guestId, expiresAt));
        return new GuestSession(token, guestId);
    }

    public Optional<String> resolveGuestId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        Instant now = clock.instant();
        GuestSessionEntry sessionEntry = guestSessions.get(token);
        if (sessionEntry == null) {
            return Optional.empty();
        }
        if (!sessionEntry.expiresAt().isAfter(now)) {
            guestSessions.remove(token, sessionEntry);
            return Optional.empty();
        }
        return Optional.of(sessionEntry.guestId());
    }

    private void evictExpiredSessions(Instant now) {
        guestSessions.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
    }

    private void evictOldestSession() {
        Optional<String> tokenToEvict = guestSessions.entrySet().stream()
            .min(Comparator.comparing(entry -> entry.getValue().expiresAt()))
            .map(Map.Entry::getKey);
        tokenToEvict.ifPresent(guestSessions::remove);
    }

    private record GuestSessionEntry(String guestId, Instant expiresAt) {
    }
}
