package com.event.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminSessionService {

    private static final Duration DEFAULT_ADMIN_SESSION_TTL = Duration.ofMinutes(30);
    private static final int DEFAULT_MAX_SESSIONS = 10_000;

    private final String adminOperatorId;
    private final String adminPassword;
    private final Clock clock;
    private final Duration sessionTtl;
    private final int maxSessions;

    private final Map<String, ActiveSessionEntry> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, RevokedSessionEntry> revokedSessions = new ConcurrentHashMap<>();

    @Autowired
    public AdminSessionService(
        @Value("${app.auth.admin-operator-id}") String adminOperatorId,
        @Value("${app.auth.admin-password}") String adminPassword
    ) {
        this(adminOperatorId, adminPassword, Clock.systemUTC(), DEFAULT_ADMIN_SESSION_TTL, DEFAULT_MAX_SESSIONS);
    }

    AdminSessionService(
        String adminOperatorId,
        String adminPassword,
        Clock clock,
        Duration sessionTtl,
        int maxSessions
    ) {
        if (adminOperatorId == null || adminOperatorId.trim().isBlank()) {
            throw new IllegalArgumentException("adminOperatorId must not be blank");
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalArgumentException("adminPassword must not be blank");
        }
        if (sessionTtl == null || sessionTtl.isZero() || sessionTtl.isNegative()) {
            throw new IllegalArgumentException("sessionTtl must be positive");
        }
        if (maxSessions <= 0) {
            throw new IllegalArgumentException("maxSessions must be positive");
        }

        this.adminOperatorId = adminOperatorId.trim();
        this.adminPassword = adminPassword;
        this.clock = clock;
        this.sessionTtl = sessionTtl;
        this.maxSessions = maxSessions;
    }

    public Optional<AdminSession> login(String operatorId, String password) {
        if (!isValidCredential(operatorId, password)) {
            return Optional.empty();
        }

        Instant now = clock.instant();
        evictExpired(now);
        if (activeSessions.size() >= maxSessions) {
            evictOldestActiveSession();
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = now.plus(sessionTtl);
        activeSessions.put(token, new ActiveSessionEntry(operatorId, expiresAt));
        return Optional.of(new AdminSession(token, operatorId, expiresAt));
    }

    public AdminTokenValidationResult resolve(String token) {
        Instant now = clock.instant();

        if (token == null || token.isBlank()) {
            evictExpired(now);
            return new AdminTokenValidationResult(AdminTokenState.UNAUTHORIZED, null);
        }

        ActiveSessionEntry activeSession = activeSessions.get(token);
        if (activeSession != null) {
            if (!activeSession.expiresAt().isAfter(now)) {
                activeSessions.remove(token, activeSession);
                return new AdminTokenValidationResult(AdminTokenState.EXPIRED, null);
            }
            return new AdminTokenValidationResult(AdminTokenState.VALID, activeSession.operatorId());
        }

        RevokedSessionEntry revokedSession = revokedSessions.get(token);
        if (revokedSession != null) {
            if (!revokedSession.expiresAt().isAfter(now)) {
                revokedSessions.remove(token, revokedSession);
                return new AdminTokenValidationResult(AdminTokenState.UNAUTHORIZED, null);
            }
            return new AdminTokenValidationResult(AdminTokenState.REVOKED, revokedSession.operatorId());
        }

        evictExpired(now);
        return new AdminTokenValidationResult(AdminTokenState.UNAUTHORIZED, null);
    }

    public void revoke(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        Instant now = clock.instant();
        evictExpired(now);
        ActiveSessionEntry removed = activeSessions.remove(token);
        if (removed != null) {
            if (revokedSessions.size() >= maxSessions) {
                evictOldestRevokedSession();
            }
            Instant expiresAt = removed.expiresAt().isAfter(now) ? removed.expiresAt() : now.plus(sessionTtl);
            revokedSessions.put(token, new RevokedSessionEntry(removed.operatorId(), expiresAt));
        }
    }

    private boolean isValidCredential(String operatorId, String password) {
        if (operatorId == null || password == null) {
            return false;
        }
        if (adminOperatorId == null || adminPassword == null) {
            return false;
        }
        return adminOperatorId.equals(operatorId.trim()) && adminPassword.equals(password);
    }

    private void evictExpired(Instant now) {
        activeSessions.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
        revokedSessions.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
    }

    private void evictOldestActiveSession() {
        Optional<String> tokenToEvict = activeSessions.entrySet().stream()
            .min(Comparator.comparing(entry -> entry.getValue().expiresAt()))
            .map(Map.Entry::getKey);
        tokenToEvict.ifPresent(activeSessions::remove);
    }

    private void evictOldestRevokedSession() {
        Optional<String> tokenToEvict = revokedSessions.entrySet().stream()
            .min(Comparator.comparing(entry -> entry.getValue().expiresAt()))
            .map(Map.Entry::getKey);
        tokenToEvict.ifPresent(revokedSessions::remove);
    }

    private record ActiveSessionEntry(String operatorId, Instant expiresAt) {
    }

    private record RevokedSessionEntry(String operatorId, Instant expiresAt) {
    }
}
