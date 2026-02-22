package com.event.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
        "app.reservation.event-date=2099-01-01",
        "app.auth.admin-operator-id=test-admin",
        "app.auth.admin-password=test-admin-password"
    }
)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class GuestFlowIntegrationTestBase {

    protected static final String ADMIN_OPERATOR_ID = "test-admin";
    protected static final String ADMIN_PASSWORD = "test-admin-password";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected GuestSession loginGuest() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return new GuestSession(loginResponse.get("accessToken").asText(), loginResponse.get("guestId").asText());
    }

    protected String loginAndGetAccessToken() throws Exception {
        return loginGuest().accessToken();
    }

    protected String loginAdminAndGetAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/admin")
                .contentType("application/json")
                .content("""
                    {
                      "operatorId": "%s",
                      "password": "%s"
                    }
                    """.formatted(ADMIN_OPERATOR_ID, ADMIN_PASSWORD)))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginResponse.get("accessToken").asText();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String checkInRequestBody(String qrCodePayload) {
        return "{\"qrCodePayload\":\"" + qrCodePayload + "\"}";
    }

    protected String toCheckInPayload(String guestId, String reservations) {
        return "event-reservation://checkin?guestId=" + guestId + "&reservations=" + reservations;
    }

    protected record GuestSession(String accessToken, String guestId) {
    }
}
