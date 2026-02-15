package com.event.checkin.api;

import com.event.checkin.CheckInRuleViolationException;
import com.event.checkin.CheckInService;
import com.event.checkin.CheckInService.CheckInHistoryItem;
import com.event.checkin.CheckInService.CheckInResult;
import com.event.reservation.api.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/event")
    public CheckInResponse checkInEvent(@RequestBody CheckInRequest request) {
        return toResponse(checkInService.checkInEvent(request.qrCodePayload()));
    }

    @PostMapping("/sessions/{sessionId}")
    public CheckInResponse checkInSession(@PathVariable String sessionId, @RequestBody CheckInRequest request) {
        return toResponse(checkInService.checkInSession(sessionId, request.qrCodePayload()));
    }

    @GetMapping
    public CheckInHistoryResponse listCheckIns(Authentication authentication) {
        List<CheckInHistoryEntry> checkIns = checkInService.listCheckInsByGuestId(authentication.getName()).stream()
            .map(this::toHistoryEntry)
            .toList();
        return new CheckInHistoryResponse(checkIns);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CheckInRuleViolationException.class)
    public ErrorResponse handleRuleViolation(CheckInRuleViolationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    private CheckInResponse toResponse(CheckInResult result) {
        return new CheckInResponse(
            result.guestId(),
            result.checkInType(),
            result.sessionId(),
            result.duplicate(),
            result.checkedInAt().toString()
        );
    }

    private CheckInHistoryEntry toHistoryEntry(CheckInHistoryItem item) {
        return new CheckInHistoryEntry(
            item.guestId(),
            item.checkInType(),
            item.sessionId(),
            item.checkedInAt().toString()
        );
    }
}
