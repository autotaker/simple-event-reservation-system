package com.event.reservation.api;

public record ErrorResponse(String code, String message) {

    public ErrorResponse(String message) {
        this("ERROR", message);
    }
}
