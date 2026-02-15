package com.event.auth;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GuestSessionService {

    private final Map<String, String> guestSessions = new ConcurrentHashMap<>();

    public GuestSession issueSession() {
        String token = UUID.randomUUID().toString().replace("-", "");
        String guestId = "guest-" + token.substring(0, 8);
        guestSessions.put(token, guestId);
        return new GuestSession(token, guestId);
    }

    public Optional<String> resolveGuestId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(guestSessions.get(token));
    }
}
