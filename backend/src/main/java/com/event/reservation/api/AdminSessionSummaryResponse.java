package com.event.reservation.api;

import java.util.List;

public record AdminSessionSummaryResponse(List<AdminSessionResponse> sessions) {
}
