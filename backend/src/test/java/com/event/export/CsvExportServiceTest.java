package com.event.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.event.checkin.CheckInService;
import com.event.reservation.ReservationService;
import com.event.reservation.ReservationService.ReservationExportRow;
import com.event.reservation.api.SessionAvailabilityStatus;
import com.event.reservation.api.SessionSummaryResponse;
import com.event.reservation.api.SessionSummaryResponse.SessionSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CsvExportServiceTest {

    @Test
    void exportReservationsCsvEscapesAndPreservesUtf8Characters() {
        ReservationService reservationService = mock(ReservationService.class);
        CheckInService checkInService = mock(CheckInService.class);
        CsvExportService csvExportService = new CsvExportService(reservationService, checkInService);

        when(reservationService.listReservationExportRows()).thenReturn(
            List.of(
                new ReservationExportRow(
                    "guest-1",
                    "session-1",
                    "セッション, \"A\"",
                    "10:30",
                    "Track \"東\""
                )
            )
        );

        String csv = csvExportService.exportReservationsCsv();

        assertThat(csv).isEqualTo(
            "guestId,sessionId,sessionTitle,startTime,track\r\n"
                + "guest-1,session-1,\"セッション, \"\"A\"\"\",10:30,\"Track \"\"東\"\"\"\r\n"
        );
    }

    @Test
    void exportReservationsCsvSanitizesDangerousFormulaPrefixes() {
        ReservationService reservationService = mock(ReservationService.class);
        CheckInService checkInService = mock(CheckInService.class);
        CsvExportService csvExportService = new CsvExportService(reservationService, checkInService);

        when(reservationService.listReservationExportRows()).thenReturn(
            List.of(
                new ReservationExportRow(
                    "=guest-1",
                    "+session-1",
                    "-title",
                    "@10:30",
                    "Track A"
                )
            )
        );

        String csv = csvExportService.exportReservationsCsv();

        assertThat(csv).isEqualTo(
            "guestId,sessionId,sessionTitle,startTime,track\r\n"
                + "'=guest-1,'+session-1,'-title,'@10:30,Track A\r\n"
        );
    }

    @Test
    void exportSessionCheckInsCsvContainsReservationAndCheckInUnionRows() {
        ReservationService reservationService = mock(ReservationService.class);
        CheckInService checkInService = mock(CheckInService.class);
        CsvExportService csvExportService = new CsvExportService(reservationService, checkInService);

        when(reservationService.listReservationExportRows()).thenReturn(
            List.of(
                new ReservationExportRow(
                    "guest-reserved",
                    "session-1",
                    "Session 1",
                    "10:30",
                    "Track A"
                )
            )
        );
        when(reservationService.listSessions()).thenReturn(
            new SessionSummaryResponse(
                List.of(
                    new SessionSummary(
                        "session-1",
                        "Session 1",
                        "10:30",
                        "Track A",
                        SessionAvailabilityStatus.OPEN
                    )
                )
            )
        );
        when(checkInService.snapshotSessionCheckIns()).thenReturn(
            Map.of(
                "session-1",
                Map.of("guest-checkedin-only", Instant.parse("2026-02-15T12:34:56Z"))
            )
        );

        String csv = csvExportService.exportSessionCheckInsCsv();

        assertThat(csv).contains("sessionId,sessionTitle,startTime,track,guestId,checkedIn,checkedInAt\r\n");
        assertThat(csv).contains("session-1,Session 1,10:30,Track A,guest-reserved,false,\r\n");
        assertThat(csv).contains("session-1,Session 1,10:30,Track A,guest-checkedin-only,true,2026-02-15T12:34:56Z\r\n");
    }

    @Test
    void exportSessionCheckInsCsvSanitizesDangerousFormulaPrefixes() {
        ReservationService reservationService = mock(ReservationService.class);
        CheckInService checkInService = mock(CheckInService.class);
        CsvExportService csvExportService = new CsvExportService(reservationService, checkInService);

        when(reservationService.listReservationExportRows()).thenReturn(
            List.of(
                new ReservationExportRow(
                    "=guest-reserved",
                    "+session-1",
                    "-Session 1",
                    "@10:30",
                    "Track A"
                )
            )
        );
        when(reservationService.listSessions()).thenReturn(
            new SessionSummaryResponse(
                List.of(
                    new SessionSummary(
                        "+session-1",
                        "-Session 1",
                        "@10:30",
                        "Track A",
                        SessionAvailabilityStatus.OPEN
                    )
                )
            )
        );
        when(checkInService.snapshotSessionCheckIns()).thenReturn(Map.of());

        String csv = csvExportService.exportSessionCheckInsCsv();

        assertThat(csv).isEqualTo(
            "sessionId,sessionTitle,startTime,track,guestId,checkedIn,checkedInAt\r\n"
                + "'+session-1,'-Session 1,'@10:30,Track A,'=guest-reserved,false,\r\n"
        );
    }
}
