package com.event.reservation.api;

import com.event.reservation.KeynoteCapacityExceededException;
import com.event.reservation.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ReservationResponse listReservations(Authentication authentication) {
        return reservationService.listReservations(authentication.getName());
    }

    @PostMapping("/keynote")
    public ReservationResponse reserveKeynote(Authentication authentication) {
        return reservationService.reserveKeynote(authentication.getName());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @org.springframework.web.bind.annotation.ExceptionHandler(KeynoteCapacityExceededException.class)
    public void handleKeynoteCapacityExceeded() {
    }
}
