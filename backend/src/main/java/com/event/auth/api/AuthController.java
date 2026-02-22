package com.event.auth.api;

import com.event.auth.AdminSession;
import com.event.auth.AdminSessionService;
import com.event.auth.GuestSession;
import com.event.auth.GuestSessionService;
import com.event.reservation.api.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GuestSessionService guestSessionService;
    private final AdminSessionService adminSessionService;

    public AuthController(GuestSessionService guestSessionService, AdminSessionService adminSessionService) {
        this.guestSessionService = guestSessionService;
        this.adminSessionService = adminSessionService;
    }

    @PostMapping("/guest")
    public ResponseEntity<GuestLoginResponse> guestLogin() {
        GuestSession session = guestSessionService.issueSession();
        GuestLoginResponse response = new GuestLoginResponse(session.token(), "Bearer", session.guestId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest request) {
        String operatorId = request.operatorId() == null ? "" : request.operatorId().trim();
        String password = request.password() == null ? "" : request.password();
        if (operatorId.isBlank() || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", "運用者IDまたはパスワードが正しくありません。"));
        }

        return adminSessionService.login(operatorId, password)
            .<ResponseEntity<?>>map(session -> ResponseEntity.ok(toAdminLoginResponse(session)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", "運用者IDまたはパスワードが正しくありません。")));
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<Void> adminLogout(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof String token) {
            adminSessionService.revoke(token);
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store")
            .build();
    }

    private AdminLoginResponse toAdminLoginResponse(AdminSession session) {
        return new AdminLoginResponse(session.token(), "Bearer", session.operatorId(), session.expiresAt());
    }
}
