package com.event.checkin.api;

import java.util.List;

public record CheckInHistoryResponse(List<CheckInHistoryEntry> checkIns) {}
