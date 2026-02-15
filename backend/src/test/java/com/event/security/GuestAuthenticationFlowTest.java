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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
        "app.reservation.event-date=2099-01-01",
        "app.auth.admin-token=test-admin-token"
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
        mockMvc.perform(get("/api/reservations/mypage"))
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
    void myPageApiReturnsReservationListAndReceptionQrPayload() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/keynote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/reservations/mypage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guestId))
            .andExpect(jsonPath("$.reservations[0]").value("keynote"))
            .andExpect(jsonPath("$.receptionQrCodePayload").value(
                "event-reservation://checkin?guestId=" + guestId + "&reservations=keynote"));
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
    void adminCanCreateAndUpdateSessionAndParticipantListReflectsIt() throws Exception {
        String token = "test-admin-token";

        MvcResult createResult = mockMvc.perform(post("/api/admin/sessions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

        JsonNode createdSession = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String createdSessionId = createdSession.get("sessionId").asText();

        mockMvc.perform(put("/api/admin/sessions/{sessionId}", createdSessionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessions[16].sessionId").value("session-16"))
            .andExpect(jsonPath("$.sessions[16].title").value("Admin Updated Session"));
    }

    @Test
    void adminSessionApiReturns403ForGuestToken() throws Exception {
        String token = loginAndGetAccessToken();

        mockMvc.perform(get("/api/admin/sessions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanExportReservationsCsvAsUtf8() throws Exception {
        MvcResult firstLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode firstLoginResponse = objectMapper.readTree(firstLoginResult.getResponse().getContentAsString());
        String firstAccessToken = firstLoginResponse.get("accessToken").asText();
        String firstGuestId = firstLoginResponse.get("guestId").asText();

        MvcResult secondLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode secondLoginResponse = objectMapper.readTree(secondLoginResult.getResponse().getContentAsString());
        String secondAccessToken = secondLoginResponse.get("accessToken").asText();
        String secondGuestId = secondLoginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondAccessToken))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("text/csv")))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("charset=UTF-8")))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations.csv\""))
            .andExpect(content().string(containsString("guestId,sessionId,sessionTitle,startTime,track")))
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains(firstGuestId + ",session-1,Session 1,10:30,Track A");
        assertThat(csv).contains(secondGuestId + ",session-2,Session 2,10:30,Track B");
    }

    @Test
    void adminCanExportSessionCheckInsCsvAsUtf8() throws Exception {
        MvcResult firstLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode firstLoginResponse = objectMapper.readTree(firstLoginResult.getResponse().getContentAsString());
        String firstAccessToken = firstLoginResponse.get("accessToken").asText();
        String firstGuestId = firstLoginResponse.get("guestId").asText();

        MvcResult secondLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode secondLoginResponse = objectMapper.readTree(secondLoginResult.getResponse().getContentAsString());
        String secondAccessToken = secondLoginResponse.get("accessToken").asText();
        String secondGuestId = secondLoginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reservations/sessions/session-2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondAccessToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken)
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(firstGuestId, "session-1"))))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/session-checkins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("text/csv")))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("charset=UTF-8")))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"session-checkins.csv\""))
            .andExpect(content().string(containsString("sessionId,sessionTitle,startTime,track,guestId,checkedIn,checkedInAt")))
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains("session-1,Session 1,10:30,Track A," + firstGuestId + ",true,");
        assertThat(csv).contains("session-2,Session 2,10:30,Track B," + secondGuestId + ",false,");
    }

    @Test
    void adminSessionCheckInsCsvIncludesGuestAfterReservationCancellation() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(guestId, "session-1"))))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        MvcResult exportResult = mockMvc.perform(get("/api/admin/exports/session-checkins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andReturn();

        String csv = exportResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains("session-1,Session 1,10:30,Track A," + guestId + ",true,");
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

    @Test
    void eventCheckInRecordsAndMarksDuplicateOnSecondScan() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        String payload = toCheckInPayload(guestId, "keynote");

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guestId))
            .andExpect(jsonPath("$.checkInType").value("EVENT"))
            .andExpect(jsonPath("$.duplicate").value(false));

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.duplicate").value(true));

        mockMvc.perform(get("/api/checkins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.checkIns.length()").value(1))
            .andExpect(jsonPath("$.checkIns[0].guestId").value(guestId))
            .andExpect(jsonPath("$.checkIns[0].checkInType").value("EVENT"));
    }

    @Test
    void sessionCheckInRecordsAndMarksDuplicateOnSecondScan() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        String payload = toCheckInPayload(guestId, "session-1");

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guestId").value(guestId))
            .andExpect(jsonPath("$.checkInType").value("SESSION"))
            .andExpect(jsonPath("$.sessionId").value("session-1"))
            .andExpect(jsonPath("$.duplicate").value(false));

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.duplicate").value(true));
    }

    @Test
    void sessionCheckInReturns400WhenQrDoesNotContainTargetSession() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        String payload = toCheckInPayload(guestId, "session-2");

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("このQRコードでは対象セッションをチェックインできません。"));
    }

    @Test
    void sessionCheckInReturns400WhenReservationWasAlreadyCanceled() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginResponse.get("accessToken").asText();
        String guestId = loginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        String payload = toCheckInPayload(guestId, "session-1");

        mockMvc.perform(delete("/api/reservations/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/sessions/session-1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType("application/json")
                .content(checkInRequestBody(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("対象ゲストはこのセッションを現在予約していません。"));
    }

    @Test
    void checkInHistoryEndpointReturnsOnlyAuthenticatedUsersRecords() throws Exception {
        MvcResult firstLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode firstLoginResponse = objectMapper.readTree(firstLoginResult.getResponse().getContentAsString());
        String firstAccessToken = firstLoginResponse.get("accessToken").asText();
        String firstGuestId = firstLoginResponse.get("guestId").asText();

        MvcResult secondLoginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode secondLoginResponse = objectMapper.readTree(secondLoginResult.getResponse().getContentAsString());
        String secondAccessToken = secondLoginResponse.get("accessToken").asText();
        String secondGuestId = secondLoginResponse.get("guestId").asText();

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken)
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(firstGuestId, "keynote"))))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/checkins/event")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondAccessToken)
                .contentType("application/json")
                .content(checkInRequestBody(toCheckInPayload(secondGuestId, "keynote"))))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/checkins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.checkIns.length()").value(1))
            .andExpect(jsonPath("$.checkIns[0].guestId").value(firstGuestId));
    }

    private String loginAndGetAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/guest"))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginResponse.get("accessToken").asText();
    }

    private String toCheckInPayload(String guestId, String reservations) {
        return "event-reservation://checkin?guestId=" + guestId + "&reservations=" + reservations;
    }

    private String checkInRequestBody(String qrCodePayload) {
        return "{\"qrCodePayload\":\"" + qrCodePayload + "\"}";
    }
}
