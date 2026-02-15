package com.event.checkin.api;

import com.event.checkin.CheckInType;

public record CheckInResponse(
    String guestId,
    CheckInType checkInType,
    String sessionId,
    boolean duplicate,
    String checkedInAt
) {}
