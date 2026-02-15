package com.event.reservation.api;

import java.util.List;

public record MyPageResponse(String guestId, List<String> reservations, String receptionQrCodePayload) {
}
