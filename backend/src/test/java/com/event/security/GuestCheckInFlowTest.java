package com.event.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class GuestCheckInFlowTest extends GuestFlowIntegrationTestBase {

    @Test
    void eventCheckInRecordsAndMarksDuplicateOnSecondScan() throws Exception {
        GuestSession guest = loginGuest();
        String payload = toCheckInPayload(guest.guestId(), "keynote");

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.checkInType").value("EVENT"))
            .andExpect(jsonPath("$.duplicate").value(false));

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.duplicate").value(true));

        mockMvc.perform(get("/api/checkins")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.checkIns.length()").value(1))
            .andExpect(jsonPath("$.checkIns[0].guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.checkIns[0].checkInType").value("EVENT"));
    }

    @Test
    void checkInWriteReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/checkins/event")
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload("guest-1", "keynote"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void checkInWriteReturns401ForInvalidToken() throws Exception {
        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload("guest-1", "keynote"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanCheckInEventForAnotherGuest() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(guest.guestId(), "keynote"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.duplicate").value(false));
    }

    @Test
    void sessionCheckInRecordsAndMarksDuplicateOnSecondScan() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        String payload = toCheckInPayload(guest.guestId(), "session-1");

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.checkInType").value("SESSION"))
            .andExpect(jsonPath("$.sessionId").value("session-1"))
            .andExpect(jsonPath("$.duplicate").value(false));

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.duplicate").value(true));
    }

    @Test
    void adminCanCheckInSessionForAnotherGuest() throws Exception {
        String adminToken = loginAdminAndGetAccessToken();
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(guest.guestId(), "session-1"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guest.guestId()))
            .andExpect(jsonPath("$.sessionId").value("session-1"));
    }

    @Test
    void eventCheckInReturns403WhenGuestTriesToWriteAnotherGuestsRecord() throws Exception {
        GuestSession firstGuest = loginGuest();
        GuestSession secondGuest = loginGuest();

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(secondGuest.guestId(), "keynote"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("チェックイン書き込み権限がありません。"));
    }

    @Test
    void sessionCheckInReturns403WhenGuestTriesToWriteAnotherGuestsRecord() throws Exception {
        GuestSession firstGuest = loginGuest();
        GuestSession secondGuest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondGuest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(secondGuest.guestId(), "session-1"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("チェックイン書き込み権限がありません。"));
    }

    @Test
    void sessionCheckInReturns400WhenQrDoesNotContainTargetSession() throws Exception {
        GuestSession guest = loginGuest();
        String payload = toCheckInPayload(guest.guestId(), "session-2");

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("このQRコードでは対象セッションをチェックインできません。"));
    }

    @Test
    void sessionCheckInReturns400WhenReservationWasAlreadyCanceled() throws Exception {
        GuestSession guest = loginGuest();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        String payload = toCheckInPayload(guest.guestId(), "session-1");

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken())))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, bearer(guest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("対象ゲストはこのセッションを現在予約していません。"));
    }

    @Test
    void checkInHistoryEndpointReturnsOnlyAuthenticatedUsersRecords() throws Exception {
        GuestSession firstGuest = loginGuest();
        GuestSession secondGuest = loginGuest();

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(firstGuest.guestId(), "keynote"))))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, bearer(secondGuest.accessToken()))
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(secondGuest.guestId(), "keynote"))))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/checkins")
                .header(HttpHeaders.AUTHORIZATION, bearer(firstGuest.accessToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.checkIns.length()").value(1))
            .andExpect(jsonPath("$.checkIns[0].guestId").value(firstGuest.guestId()));
    }
}
