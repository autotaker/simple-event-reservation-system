package com.event.checkin;

import com.event.checkin.CheckInQrPayloadParser.CheckInQrCodeData;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CheckInWriteAuthorizationService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_GUEST = "ROLE_GUEST";
    private static final String FORBIDDEN_MESSAGE = "チェックイン書き込み権限がありません。";

    private final CheckInQrPayloadParser qrPayloadParser;

    public CheckInWriteAuthorizationService(CheckInQrPayloadParser qrPayloadParser) {
        this.qrPayloadParser = qrPayloadParser;
    }

    public void authorize(Authentication authentication, String qrCodePayload) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CheckInAuthorizationException(FORBIDDEN_MESSAGE);
        }

        if (hasRole(authentication, ROLE_ADMIN)) {
            return;
        }

        if (!hasRole(authentication, ROLE_GUEST)) {
            throw new CheckInAuthorizationException(FORBIDDEN_MESSAGE);
        }

        CheckInQrCodeData qrCodeData = qrPayloadParser.parse(qrCodePayload);
        if (!authentication.getName().equals(qrCodeData.guestId())) {
            throw new CheckInAuthorizationException(FORBIDDEN_MESSAGE);
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
