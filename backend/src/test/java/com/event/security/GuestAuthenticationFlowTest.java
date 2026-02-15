package com.event.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(
    properties = {
        "spring.autoconfigure.exclude="
            + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
            + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
            + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "app.reservation.keynote-capacity=2",
        "app.reservation.regular-capacity=1",
        "app.reservation.event-date=2099-01-01"
    }
)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GuestAuthenticationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void guestLoginIssuesToken() throws Exception {
        mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.guestId").value(org.hamcrest.Matchers.startsWith("guest-")));
    }

    @Test
    void protectedApiReturns401WithoutLogin() throws Exception {
        mockMvc.perform(get("/api/reservations"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void reservationApiIsAvailableForGuestLogin() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guestId))
            .andExpect(jsonPath("$.reservations").isEmpty())
            .andExpect(jsonPath("$.registered").value(false));
    }

    @Test
    void sessionListApiIsAvailableForGuestLogin() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();

        mockMvc.perform(get("/api/reservations/sessions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessions.length()").value(16))
            .andExpect(jsonPath("$.sessions[0].sessionId").value("keynote"))
            .andExpect(jsonPath("$.sessions[0].availabilityStatus").value("FEW_LEFT"))
            .andExpect(jsonPath("$.sessions[1].startTime").isNotEmpty())
            .andExpect(jsonPath("$.sessions[1].track").isNotEmpty());
    }

    @Test
    void keynoteReservationRegistersGuest() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guestId))
            .andExpect(jsonPath("$.reservations[0]").value("keynote"))
            .andExpect(jsonPath("$.registered").value(true));

        mockMvc.perform(get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations[0]").value("keynote"))
            .andExpect(jsonPath("$.registered").value(true));
    }

    @Test
    void keynoteReservationReturns409WhenCapacityExceeded() throws Exception {
        String firstToken = loginAndGetAccessToken();
        String secondToken = loginAndGetAccessToken();
        String thirdToken = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + thirdToken))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("セッションは満席です。"));
    }

    @Test
    void regularSessionReservationReplacesForSameTimeslot() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations.length()").value(1))
            .andExpect(jsonPath("$.reservations[0]").value("session-2"));
    }

    @Test
    void regularSessionReservationKeepsFiveSessionsWhenReplacingTimeslot() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-4")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-7")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-10")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-13")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-15")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations.length()").value(5))
            .andExpect(jsonPath("$.reservations[4]").value("session-15"));
    }

    @Test
    void regularSessionCancellationReturnsUpdatedReservations() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations").isEmpty())
            .andExpect(jsonPath("$.registered").value(false));
    }

    @Test
    void regularSessionReservationReturns409WhenCapacityExceeded() throws Exception {
        String firstToken = loginAndGetAccessToken();
        String secondToken = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondToken))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("セッションは満席です。"));
    }

    private String loginAndGetAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginResponse.get("accessToken").asText();
    }
}
