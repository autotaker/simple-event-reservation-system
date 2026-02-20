package com.event.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class GuestReservationFlowTest extends GuestFlowIntegrationTestBase {

    @Test
    void keynoteReservationRegistersGuest() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.reservations[0]").value("keynote"))
            .andExpect(jsonPath("$.registered").value(true));

        mockMvc.perform(get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
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
                .header(HttpHeaders.AUTHORIZATION, bearer(firstToken)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondToken)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, bearer(thirdToken)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("セッションは満席です。"));
    }

    @Test
    void regularSessionReservationReplacesForSameTimeslot() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations.length()").value(1))
            .andExpect(jsonPath("$.reservations[0]").value("session-2"));
    }

    @Test
    void regularSessionReservationKeepsFiveSessionsWhenReplacingTimeslot() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-4")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-7")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-10")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-13")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-15")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations.length()").value(5))
            .andExpect(jsonPath("$.reservations[4]").value("session-15"));
    }

    @Test
    void regularSessionCancellationReturnsUpdatedReservations() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservations").isEmpty())
            .andExpect(jsonPath("$.registered").value(false));
    }

    @Test
    void regularSessionReservationReturns409WhenCapacityExceeded() throws Exception {
        String firstToken = loginAndGetAccessToken();
        String secondToken = loginAndGetAccessToken();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstToken)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondToken)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("セッションは満席です。"));
    }

    @Test
    void deleteReservationPreflightAllowsBrowserCorsRequest() throws Exception {
        mockMvc.perform(options("/api/reservations/sessions/session-1")
                .header(HttpHeaders.ORIGIN, "http://127.0.0.1:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:5173"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("DELETE")));
    }
}
