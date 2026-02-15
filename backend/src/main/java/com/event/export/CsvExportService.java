package com.event.export;

import com.event.checkin.CheckInService;
import com.event.reservation.ReservationService;
import com.event.reservation.ReservationService.ReservationExportRow;
import com.event.reservation.api.SessionSummaryResponse.SessionSummary;
import java.time.Instant;
import java.util.LinkedHashMap;
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
        List<SessionSummary> sessionSummaries = reservationService.listSessions().sessions();
        Map<String, SessionMetadata> sessionMetadataById = sessionSummaries.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    SessionSummary::sessionId,
                    summary -> new SessionMetadata(summary.title(), summary.startTime(), summary.track())
                )
            );
        Map<String, Integer> sessionOrderById = buildSessionOrderById(sessionSummaries);

        Map<RowKey, SessionCheckInExportRow> rowsByKey = new LinkedHashMap<>();
        for (ReservationExportRow reservation : reservations) {
            RowKey key = new RowKey(reservation.sessionId(), reservation.guestId());
            rowsByKey.put(
                key,
                new SessionCheckInExportRow(
                    reservation.sessionId(),
                    reservation.sessionTitle(),
                    reservation.startTime(),
                    reservation.track(),
                    reservation.guestId(),
                    false,
                    null
                )
            );
        }

        for (Map.Entry<String, Map<String, Instant>> sessionEntry : checkInSnapshot.entrySet()) {
            String sessionId = sessionEntry.getKey();
            SessionMetadata metadata = sessionMetadataById.getOrDefault(sessionId, SessionMetadata.empty());
            for (Map.Entry<String, Instant> guestEntry : sessionEntry.getValue().entrySet()) {
                String guestId = guestEntry.getKey();
                RowKey key = new RowKey(sessionId, guestId);
                SessionCheckInExportRow current = rowsByKey.get(key);
                rowsByKey.put(
                    key,
                    new SessionCheckInExportRow(
                        sessionId,
                        current == null ? metadata.sessionTitle() : current.sessionTitle(),
                        current == null ? metadata.startTime() : current.startTime(),
                        current == null ? metadata.track() : current.track(),
                        guestId,
                        true,
                        guestEntry.getValue()
                    )
                );
            }
        }

        rowsByKey.values().stream()
            .sorted(
                java.util.Comparator.comparingInt(
                    (SessionCheckInExportRow row) -> sessionOrderById.getOrDefault(row.sessionId(), Integer.MAX_VALUE)
                ).thenComparing(SessionCheckInExportRow::sessionId)
                    .thenComparing(SessionCheckInExportRow::guestId)
            )
            .forEach(
                row -> appendLine(
                    csvBuilder,
                    List.of(
                        row.sessionId(),
                        row.sessionTitle(),
                        row.startTime(),
                        row.track(),
                        row.guestId(),
                        Boolean.toString(row.checkedIn()),
                        row.checkedInAt() == null ? "" : row.checkedInAt().toString()
                    )
                )
            );

        return csvBuilder.toString();
    }

    private Map<String, Integer> buildSessionOrderById(List<SessionSummary> sessions) {
        Map<String, Integer> orderById = new LinkedHashMap<>();
        for (int index = 0; index < sessions.size(); index++) {
            orderById.put(sessions.get(index).sessionId(), index);
        }
        return orderById;
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

    private record SessionMetadata(String sessionTitle, String startTime, String track) {
        private static SessionMetadata empty() {
            return new SessionMetadata("", "", "");
        }
    }

    private record RowKey(String sessionId, String guestId) {}

    private record SessionCheckInExportRow(
        String sessionId,
        String sessionTitle,
        String startTime,
        String track,
        String guestId,
        boolean checkedIn,
        Instant checkedInAt
    ) {}
}
