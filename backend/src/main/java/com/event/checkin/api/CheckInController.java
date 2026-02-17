package com.event.checkin.api;

import com.event.checkin.CheckInAuthorizationException;
import com.event.checkin.CheckInRuleViolationException;
import com.event.checkin.CheckInService;
import com.event.checkin.CheckInService.CheckInHistoryItem;
import com.event.checkin.CheckInService.CheckInResult;
import com.event.checkin.CheckInWriteAuthorizationService;
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
    private final CheckInWriteAuthorizationService checkInWriteAuthorizationService;

    public CheckInController(
        CheckInService checkInService,
        CheckInWriteAuthorizationService checkInWriteAuthorizationService
    ) {
        this.checkInService = checkInService;
        this.checkInWriteAuthorizationService = checkInWriteAuthorizationService;
    }

    @PostMapping("/event")
    public CheckInResponse checkInEvent(Authentication authentication, @RequestBody CheckInRequest request) {
        checkInWriteAuthorizationService.authorize(authentication, request.qrCodePayload());
        return toResponse(checkInService.checkInEvent(request.qrCodePayload()));
    }

    @PostMapping("/sessions/{sessionId}")
    public CheckInResponse checkInSession(
        Authentication authentication,
        @PathVariable String sessionId,
        @RequestBody CheckInRequest request
    ) {
        checkInWriteAuthorizationService.authorize(authentication, request.qrCodePayload());
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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CheckInAuthorizationException.class)
    public ErrorResponse handleAuthorizationViolation(CheckInAuthorizationException exception) {
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
