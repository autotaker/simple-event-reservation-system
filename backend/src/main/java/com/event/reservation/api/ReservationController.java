package com.event.reservation.api;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @GetMapping
    public ReservationResponse listReservations(Authentication authentication) {
        return new ReservationResponse(authentication.getName(), List.of("welcome-session"));
    }
}
