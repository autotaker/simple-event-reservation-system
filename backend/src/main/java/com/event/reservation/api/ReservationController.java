package com.event.reservation.api;

import com.event.reservation.ReservationRuleViolationException;
import com.event.reservation.ReservationService;
import com.event.reservation.SessionCapacityExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/sessions")
    public SessionSummaryResponse listSessions() {
        return reservationService.listSessions();
    }

    @PostMapping("/keynote")
    public ReservationResponse reserveKeynote(Authentication authentication) {
        return reservationService.reserveKeynote(authentication.getName());
    }

    @PostMapping("/sessions/{sessionId}")
    public ReservationResponse reserveSession(Authentication authentication, @PathVariable String sessionId) {
        return reservationService.reserveSession(authentication.getName(), sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ReservationResponse cancelSession(Authentication authentication, @PathVariable String sessionId) {
        return reservationService.cancelSessionReservation(authentication.getName(), sessionId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReservationRuleViolationException.class)
    public ErrorResponse handleRuleViolation(ReservationRuleViolationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SessionCapacityExceededException.class)
    public ErrorResponse handleSessionCapacityExceeded(SessionCapacityExceededException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
