package com.event.reservation;

public class SessionCapacityExceededException extends RuntimeException {

    private final String sessionId;

    public SessionCapacityExceededException(String sessionId) {
        super("セッションは満席です。");
        this.sessionId = sessionId;
    }

    public String sessionId() {
        return sessionId;
    }
}
