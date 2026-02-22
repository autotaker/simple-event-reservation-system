package com.event.auth;

import java.time.Instant;

public record AdminSession(String token, String operatorId, Instant expiresAt) {
}
