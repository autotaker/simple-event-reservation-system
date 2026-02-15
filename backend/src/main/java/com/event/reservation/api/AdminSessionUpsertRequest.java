package com.event.reservation.api;

public record AdminSessionUpsertRequest(
    String title,
    String startTime,
    String track,
    Integer capacity
) {
}
