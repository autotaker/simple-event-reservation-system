package com.event.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class GuestAdminFlowTest extends GuestFlowIntegrationTestBase {

    @Test
    void adminCanCreateAndUpdateSessionAndParticipantListReflectsIt() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        MvcResult createResult = mockMvc.perform(post("/api/admin/sessions")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                .contentType("application/json")
                .content("""
                    {
                      "title": "Admin Created Session",
                      "startTime": "16:30",
                      "track": "Track D",
                      "capacity": 10
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value("session-16"))
            .andExpect(jsonPath("$.title").value("Admin Created Session"))
            .andReturn();

        String createdSessionId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("sessionId")
            .asText();

        mockMvc.perform(put("/api/admin/sessions/{sessionId}", createdSessionId)
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                .contentType("application/json")
                .content("""
                    {
                      "title": "Admin Updated Session",
                      "startTime": "16:45",
                      "track": "Track E",
                      "capacity": 15
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Admin Updated Session"))
            .andExpect(jsonPath("$.startTime").value("16:45"))
            .andExpect(jsonPath("$.track").value("Track E"))
            .andExpect(jsonPath("$.capacity").value(15));

        mockMvc.perform(get("/api/reservations/sessions")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessions[16].sessionId").value("session-16"))
            .andExpect(jsonPath("$.sessions[16].title").value("Admin Updated Session"));
    }

    @Test
    void adminSessionApiReturns403ForGuestToken() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(get("/api/admin/sessions")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanExportReservationsCsvAsUtf8() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        GuestSession firstGuest = loginGuest();
        GuestSession secondGuest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken())))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondGuest.accessToken())))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/reservations")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("text/csv")))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("charset=UTF-8")))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations.csv\""))
            .andExpect(content().string(containsString("guestId,sessionId,sessionTitle,startTime,track")))
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains(firstGuest.guestId() + ",session-1,Session 1,10:30,Track A");
        assertThat(csv).contains(secondGuest.guestId() + ",session-2,Session 2,10:30,Track B");
    }

    @Test
    void adminCanExportSessionCheckInsCsvAsUtf8() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        GuestSession firstGuest = loginGuest();
        GuestSession secondGuest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken())))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondGuest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(firstGuest.guestId(), "session-1"))))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/session-checkins")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("text/csv")))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("charset=UTF-8")))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"session-checkins.csv\""))
            .andExpect(content().string(containsString("sessionId,sessionTitle,startTime,track,guestId,checkedIn,checkedInAt")))
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains("session-1,Session 1,10:30,Track A," + firstGuest.guestId() + ",true,");
        assertThat(csv).contains("session-2,Session 2,10:30,Track B," + secondGuest.guestId() + ",false,");
    }

    @Test
    void adminSessionCheckInsCsvIncludesGuestAfterReservationCancellation() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(guest.guestId(), "session-1"))))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/session-checkins")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains("session-1,Session 1,10:30,Track A," + guest.guestId() + ",true,");
    }

    @Test
    void putAdminSessionPreflightAllowsBrowserCorsRequest() throws Exception {
        mockMvc.perform(options("/api/admin/sessions/session-1")
                .header(HttpHeaders.ORIGIN, "http://127.0.0.1:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:5173"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("PUT")));
    }

    @Test
    void revokedAdminTokenReturns401WithRevokedCode() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();

        mockMvc.perform(post("/api/auth/admin/logout")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/sessions")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("REVOKED"))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
