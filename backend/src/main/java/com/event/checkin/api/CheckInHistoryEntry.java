package com.event.checkin.api;

import com.event.checkin.CheckInType;

public record CheckInHistoryEntry(
    String guestId,
    CheckInType checkInType,
    String sessionId,
    String checkedInAt
) {}
