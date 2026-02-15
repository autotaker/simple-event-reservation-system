package com.event.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GuestAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<SimpleGrantedAuthority> GUEST_AUTHORITIES =
        List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
    private static final List<SimpleGrantedAuthority> ADMIN_AUTHORITIES =
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    private final GuestSessionService guestSessionService;
    private final String adminToken;

    public GuestAuthenticationFilter(
        GuestSessionService guestSessionService,
        @Value("${app.auth.admin-token:}") String adminToken
    ) {
        this.guestSessionService = guestSessionService;
        this.adminToken = adminToken;
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
            if (isAdminToken(token)) {
                UsernamePasswordAuthenticationToken adminAuth =
                    new UsernamePasswordAuthenticationToken("admin", null, ADMIN_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(adminAuth);
                filterChain.doFilter(request, response);
                return;
            }
            guestSessionService.resolveGuestId(token).ifPresent(guestId -> {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(guestId, null, GUEST_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(auth);
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

    private boolean isAdminToken(String token) {
        return adminToken != null && !adminToken.isBlank() && adminToken.equals(token);
    }
}
