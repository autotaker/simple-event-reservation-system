package com.event.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class GuestAuthenticationApiFlowTest extends GuestFlowIntegrationTestBase {

    @Test
    void guestLoginIssuesToken() throws Exception {
        mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.guestId").value(org.hamcrest.Matchers.startsWith("guest-")));
    }

    @Test
    void adminLoginIssuesToken() throws Exception {
        mockMvc.perform(post("/api/auth/admin")
                .contentType("application/json")
                .content("""
                    {
                      "operatorId": "%s",
                      "password": "%s"
                    }
                    """.formatted(ADMIN_OPERATOR_ID, ADMIN_PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.operatorId").value(ADMIN_OPERATOR_ID))
            .andExpect(jsonPath("$.expiresAt").isNotEmpty());
    }

    @Test
    void adminLoginReturns401ForInvalidCredential() throws Exception {
        mockMvc.perform(post("/api/auth/admin")
                .contentType("application/json")
                .content("""
                    {
                      "operatorId": "unknown",
                      "password": "invalid"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void protectedApiReturns401WithoutLogin() throws Exception {
        mockMvc.perform(get("/api/reservations"))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reservations/mypage"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void reservationApiIsAvailableForGuestLogin() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.reservations").isEmpty())
            .andExpect(jsonPath("$.registered").value(false));
    }

    @Test
    void adminTokenDoesNotLeakIntoAuthenticatedName() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();

        mockMvc.perform(get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(ADMIN_OPERATOR_ID));
    }

    @Test
    void myPageApiReturnsReservationListAndReceptionQrPayload() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/reservations/mypage")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.reservations[0]").value("keynote"))
            .andExpect(jsonPath("$.receptionQrCodePayload").value(
                "event-reservation://checkin?guestId=" + guest.guestId() + "&reservations=keynote"));
    }

    @Test
    void sessionListApiIsAvailableForGuestLogin() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(get("/api/reservations/sessions")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessions.length()").value(16))
            .andExpect(jsonPath("$.sessions[0].sessionId").value("keynote"))
            .andExpect(jsonPath("$.sessions[0].availabilityStatus").value("FEW_LEFT"))
            .andExpect(jsonPath("$.sessions[1].startTime").isNotEmpty())
            .andExpect(jsonPath("$.sessions[1].track").isNotEmpty());
    }
}
