package com.event.auth.api;

public record GuestLoginResponse(String accessToken, String tokenType, String guestId) {
}
