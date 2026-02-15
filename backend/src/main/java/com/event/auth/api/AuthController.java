package com.event.auth.api;

import com.event.auth.GuestSession;
import com.event.auth.GuestSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GuestSessionService guestSessionService;

    public AuthController(GuestSessionService guestSessionService) {
        this.guestSessionService = guestSessionService;
    }

    @PostMapping("/guest")
    public ResponseEntity<GuestLoginResponse> guestLogin() {
        GuestSession session = guestSessionService.issueSession();
        GuestLoginResponse response = new GuestLoginResponse(session.token(), "Bearer", session.guestId());
        return ResponseEntity.ok(response);
    }
}
