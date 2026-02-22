package com.event.reservation.api;

import java.util.List;

public record SessionSummaryResponse(List<SessionSummary> sessions) {

    public record SessionSummary(
        String sessionId,
        String title,
        String startTime,
        String track,
        Integer displayOrder,
        SessionAvailabilityStatus availabilityStatus
    ) {
    }
}
