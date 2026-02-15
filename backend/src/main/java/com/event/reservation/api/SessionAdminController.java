package com.event.reservation.api;

import com.event.reservation.ReservationRuleViolationException;
import com.event.reservation.ReservationService;
import com.event.reservation.SessionCapacityExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sessions")
public class SessionAdminController {

    private final ReservationService reservationService;

    public SessionAdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public AdminSessionSummaryResponse listSessions() {
        return reservationService.listAdminSessions();
    }

    @PostMapping
    public AdminSessionResponse createSession(@RequestBody AdminSessionUpsertRequest request) {
        return reservationService.createSession(request.title(), request.startTime(), request.track(), request.capacity());
    }

    @PutMapping("/{sessionId}")
    public AdminSessionResponse updateSession(@PathVariable String sessionId, @RequestBody AdminSessionUpsertRequest request) {
        return reservationService.updateSession(sessionId, request.title(), request.startTime(), request.track(), request.capacity());
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
