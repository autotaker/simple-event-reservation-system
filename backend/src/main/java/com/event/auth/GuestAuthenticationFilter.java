package com.event.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GuestAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR_CODE_ATTR = "auth.error.code";

    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<SimpleGrantedAuthority> GUEST_AUTHORITIES =
        List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
    private static final List<SimpleGrantedAuthority> ADMIN_AUTHORITIES =
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    private final GuestSessionService guestSessionService;
    private final AdminSessionService adminSessionService;

    public GuestAuthenticationFilter(
        GuestSessionService guestSessionService,
        AdminSessionService adminSessionService
    ) {
        this.guestSessionService = guestSessionService;
        this.adminSessionService = adminSessionService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuthentication == null) {
            String token = extractBearerToken(request);
            AdminTokenValidationResult adminResult = adminSessionService.resolve(token);
            if (adminResult.isValid()) {
                UsernamePasswordAuthenticationToken adminAuth =
                    new UsernamePasswordAuthenticationToken(
                        new AdminAuthenticationPrincipal(adminResult.operatorId(), token),
                        null,
                        ADMIN_AUTHORITIES
                    );
                SecurityContextHolder.getContext().setAuthentication(adminAuth);
                request.removeAttribute(AUTH_ERROR_CODE_ATTR);
                filterChain.doFilter(request, response);
                return;
            }

            if (adminResult.state() == AdminTokenState.EXPIRED || adminResult.state() == AdminTokenState.REVOKED) {
                request.setAttribute(AUTH_ERROR_CODE_ATTR, adminResult.state().name());
            }

            guestSessionService.resolveGuestId(token).ifPresent(guestId -> {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(guestId, null, GUEST_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.removeAttribute(AUTH_ERROR_CODE_ATTR);
            });
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
