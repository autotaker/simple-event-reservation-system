package com.event.auth.api;

import java.time.Instant;

public record AdminLoginResponse(
    String accessToken,
    String tokenType,
    String operatorId,
    Instant expiresAt
) {
}
