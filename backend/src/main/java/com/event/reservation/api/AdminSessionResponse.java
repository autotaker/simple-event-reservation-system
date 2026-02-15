package com.event.reservation.api;

public record AdminSessionResponse(
    String sessionId,
    String title,
    String startTime,
    String track,
    int capacity,
    int reservedCount
) {
}
