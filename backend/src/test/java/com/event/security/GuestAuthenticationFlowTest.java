package com.event.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        "app.reservation.keynote-capacity=2"
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
            .andExpect(status().isConflict());
    }

    private String loginAndGetAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginResponse.get("accessToken").asText();
    }
}
