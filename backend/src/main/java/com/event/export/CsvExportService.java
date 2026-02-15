package com.event.export;

import com.event.checkin.CheckInService;
import com.event.reservation.ReservationService;
import com.event.reservation.ReservationService.ReservationExportRow;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CsvExportService {

    private static final String LINE_SEPARATOR = "\r\n";

    private final ReservationService reservationService;
    private final CheckInService checkInService;

    public CsvExportService(ReservationService reservationService, CheckInService checkInService) {
        this.reservationService = reservationService;
        this.checkInService = checkInService;
    }

    public String exportReservationsCsv() {
        StringBuilder csvBuilder = new StringBuilder();
        appendLine(csvBuilder, List.of("guestId", "sessionId", "sessionTitle", "startTime", "track"));

        for (ReservationExportRow row : reservationService.listReservationExportRows()) {
            appendLine(
                csvBuilder,
                List.of(
                    row.guestId(),
                    row.sessionId(),
                    row.sessionTitle(),
                    row.startTime(),
                    row.track()
                )
            );
        }
        return csvBuilder.toString();
    }

    public String exportSessionCheckInsCsv() {
        StringBuilder csvBuilder = new StringBuilder();
        appendLine(
            csvBuilder,
            List.of("sessionId", "sessionTitle", "startTime", "track", "guestId", "checkedIn", "checkedInAt")
        );

        List<ReservationExportRow> reservations = reservationService.listReservationExportRows();
        Map<String, Map<String, Instant>> checkInSnapshot = checkInService.snapshotSessionCheckIns();

        for (ReservationExportRow row : reservations) {
            Instant checkedInAt = checkInSnapshot
                .getOrDefault(row.sessionId(), Map.of())
                .get(row.guestId());
            appendLine(
                csvBuilder,
                List.of(
                    row.sessionId(),
                    row.sessionTitle(),
                    row.startTime(),
                    row.track(),
                    row.guestId(),
                    Boolean.toString(checkedInAt != null),
                    checkedInAt == null ? "" : checkedInAt.toString()
                )
            );
        }

        return csvBuilder.toString();
    }

    private void appendLine(StringBuilder csvBuilder, List<String> values) {
        String joined = String.join(",", values.stream().map(this::escapeCsv).toList());
        csvBuilder.append(joined).append(LINE_SEPARATOR);
    }

    private String escapeCsv(String rawValue) {
        String normalizedValue = rawValue == null ? "" : rawValue;
        String escapedValue = normalizedValue.replace("\"", "\"\"");
        boolean needsQuote = escapedValue.contains(",")
            || escapedValue.contains("\"")
            || escapedValue.contains("\n")
            || escapedValue.contains("\r");
        if (needsQuote) {
            return "\"" + escapedValue + "\"";
        }
        return escapedValue;
    }
}
