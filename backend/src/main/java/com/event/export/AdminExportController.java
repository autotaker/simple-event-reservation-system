package com.event.export;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/exports")
public class AdminExportController {

    private static final MediaType TEXT_CSV_UTF8 = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final CsvExportService csvExportService;

    public AdminExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping(value = "/reservations", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<String> exportReservationsCsv() {
        return csvResponse("reservations.csv", csvExportService.exportReservationsCsv());
    }

    @GetMapping(value = "/session-checkins", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<String> exportSessionCheckInsCsv() {
        return csvResponse("session-checkins.csv", csvExportService.exportSessionCheckInsCsv());
    }

    private ResponseEntity<String> csvResponse(String filename, String body) {
        return ResponseEntity.ok()
            .contentType(TEXT_CSV_UTF8)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(body);
    }
}
