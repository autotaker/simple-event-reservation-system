# Complexity / Unit Test Coverage Report

- Generated at (UTC): 2026-02-23T00:46:43.486310+00:00

## Backend

- Complexity summary: Total NLOC=1669, AvgCCN=2.2, Functions=118
- Coverage (LINE): 91.62% (711/776)
- Coverage (BRANCH): 71.76% (216/301)

### Backend complexity hotspots (by CCN)
- AdminSessionService::AdminSessionService (/Users/autotaker/git/simple-event-reservation-system/backend/src/main/java/com/event/auth/AdminSessionService.java:38) - CCN 9, NLOC 25
- ReservationService::validateSessionFields (/Users/autotaker/git/simple-event-reservation-system/backend/src/main/java/com/event/reservation/ReservationService.java:253) - CCN 9, NLOC 14
- CsvExportService::exportSessionCheckInsCsv (/Users/autotaker/git/simple-event-reservation-system/backend/src/main/java/com/event/export/CsvExportService.java:46) - CCN 8, NLOC 77
- CheckInQrPayloadParser::parse (/Users/autotaker/git/simple-event-reservation-system/backend/src/main/java/com/event/checkin/CheckInQrPayloadParser.java:19) - CCN 7, NLOC 25
- AdminSessionService::resolve (/Users/autotaker/git/simple-event-reservation-system/backend/src/main/java/com/event/auth/AdminSessionService.java:82) - CCN 7, NLOC 24

## Frontend

- Complexity summary (app-only): Total NLOC=889, AvgCCN=3.5, Functions=42
- Coverage (lines/statements): 63.62% (1275/2004)
- Coverage (branches): 36.61% (26/71)
- Coverage (functions): 15.78% (12/76)

### Frontend low coverage files (by line coverage)
- frontend/src/components/participant/ParticipantSessionCard.vue - lines 0%, functions 100%, branches 100%
- frontend/src/main.ts - lines 0%, functions 0%, branches 0%
- frontend/src/components/admin/AdminAccessGate.vue - lines 0%, functions 100%, branches 100%
- frontend/src/router/index.ts - lines 0%, functions 0%, branches 0%
- frontend/src/composables/useReservationApp.ts - lines 23.48%, functions 21.05%, branches 50%
- frontend/src/components/participant/ParticipantReservationPanel.vue - lines 65.71%, functions 0%, branches 0%
- frontend/src/views/ParticipantView.vue - lines 70.3%, functions 0%, branches 41.17%
- frontend/src/views/AdminAuthView.vue - lines 75%, functions 0%, branches 12.5%

### Frontend complexity hotspots (by CCN)
- useReservationApp (frontend/src/composables/useReservationApp.ts:152) - CCN 12, NLOC 84
- reserveSession (frontend/src/composables/useReservationApp.ts:374) - CCN 10, NLOC 37
- downloadAdminCsv (frontend/src/composables/useReservationApp.ts:456) - CCN 9, NLOC 33
- (anonymous) (frontend/src/composables/useReservationApp.ts:866) - CCN 9, NLOC 33
- loadAdminSessions (frontend/src/composables/useReservationApp.ts:314) - CCN 9, NLOC 27

## Source Artifacts
- backend_complexity: /Users/autotaker/git/simple-event-reservation-system/reports/backend_complexity_lizard.txt
- frontend_complexity: /Users/autotaker/git/simple-event-reservation-system/reports/frontend_complexity_lizard_app_only.txt
- backend_jacoco_xml: /Users/autotaker/git/simple-event-reservation-system/backend/build/reports/jacoco/test/jacocoTestReport.xml
- frontend_coverage_summary_json: /Users/autotaker/git/simple-event-reservation-system/frontend/coverage/coverage-summary.json
