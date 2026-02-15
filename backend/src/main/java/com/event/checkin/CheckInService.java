package com.event.checkin;

import com.event.reservation.ReservationService;
import java.util.Arrays;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {

    private static final String QR_SCHEME = "event-reservation";
    private static final String QR_HOST = "checkin";
    private final Object checkInLock = new Object();
    private final ReservationService reservationService;
    private final Clock clock;
    private final Map<String, Instant> eventCheckInsByGuest = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Instant>> sessionCheckInsBySession = new ConcurrentHashMap<>();

    @Autowired
    public CheckInService(ReservationService reservationService) {
        this(reservationService, Clock.systemDefaultZone());
    }

    private CheckInService(ReservationService reservationService, Clock clock) {
        this.reservationService = reservationService;
        this.clock = clock;
    }

    public CheckInResult checkInEvent(String qrCodePayload) {
        QrCodeData qrCodeData = parseQrCodePayload(qrCodePayload);
        Instant now = Instant.now(clock);

        synchronized (checkInLock) {
            Instant checkedInAt = eventCheckInsByGuest.putIfAbsent(qrCodeData.guestId(), now);
            if (checkedInAt != null) {
                return new CheckInResult(qrCodeData.guestId(), CheckInType.EVENT, null, true, checkedInAt);
            }
            return new CheckInResult(qrCodeData.guestId(), CheckInType.EVENT, null, false, now);
        }
    }

    public CheckInResult checkInSession(String sessionId, String qrCodePayload) {
        if (!reservationService.sessionExists(sessionId)) {
            throw new CheckInRuleViolationException("指定されたセッションは存在しません。");
        }
        QrCodeData qrCodeData = parseQrCodePayload(qrCodePayload);
        if (!qrCodeData.reservations().contains(sessionId)) {
            throw new CheckInRuleViolationException("このQRコードでは対象セッションをチェックインできません。");
        }
        if (!reservationService.hasReservation(qrCodeData.guestId(), sessionId)) {
            throw new CheckInRuleViolationException("対象ゲストはこのセッションを現在予約していません。");
        }
        Instant now = Instant.now(clock);

        synchronized (checkInLock) {
            Map<String, Instant> sessionCheckIns = sessionCheckInsBySession.computeIfAbsent(
                sessionId,
                key -> new ConcurrentHashMap<>()
            );
            Instant checkedInAt = sessionCheckIns.putIfAbsent(qrCodeData.guestId(), now);
            if (checkedInAt != null) {
                return new CheckInResult(qrCodeData.guestId(), CheckInType.SESSION, sessionId, true, checkedInAt);
            }
            return new CheckInResult(qrCodeData.guestId(), CheckInType.SESSION, sessionId, false, now);
        }
    }

    public List<CheckInHistoryItem> listCheckIns() {
        List<CheckInHistoryItem> checkIns = new ArrayList<>();
        for (Map.Entry<String, Instant> eventCheckInEntry : eventCheckInsByGuest.entrySet()) {
            checkIns.add(
                new CheckInHistoryItem(
                    eventCheckInEntry.getKey(),
                    CheckInType.EVENT,
                    null,
                    eventCheckInEntry.getValue()
                )
            );
        }
        for (Map.Entry<String, Map<String, Instant>> sessionCheckInEntry : sessionCheckInsBySession.entrySet()) {
            for (Map.Entry<String, Instant> guestCheckInEntry : sessionCheckInEntry.getValue().entrySet()) {
                checkIns.add(
                    new CheckInHistoryItem(
                        guestCheckInEntry.getKey(),
                        CheckInType.SESSION,
                        sessionCheckInEntry.getKey(),
                        guestCheckInEntry.getValue()
                    )
                );
            }
        }
        checkIns.sort(Comparator.comparing(CheckInHistoryItem::checkedInAt).reversed());
        return List.copyOf(checkIns);
    }

    public List<CheckInHistoryItem> listCheckInsByGuestId(String guestId) {
        return listCheckIns().stream()
            .filter(item -> item.guestId().equals(guestId))
            .toList();
    }

    public Map<String, Map<String, Instant>> snapshotSessionCheckIns() {
        synchronized (checkInLock) {
            Map<String, Map<String, Instant>> snapshot = new HashMap<>();
            for (Map.Entry<String, Map<String, Instant>> entry : sessionCheckInsBySession.entrySet()) {
                snapshot.put(entry.getKey(), Map.copyOf(entry.getValue()));
            }
            return Map.copyOf(snapshot);
        }
    }

    private QrCodeData parseQrCodePayload(String qrCodePayload) {
        if (qrCodePayload == null || qrCodePayload.isBlank()) {
            throw new CheckInRuleViolationException("QRコードの内容が空です。");
        }
        URI uri;
        try {
            uri = URI.create(qrCodePayload.trim());
        } catch (IllegalArgumentException exception) {
            throw new CheckInRuleViolationException("QRコードの形式が不正です。");
        }
        if (!QR_SCHEME.equals(uri.getScheme()) || !QR_HOST.equals(uri.getHost())) {
            throw new CheckInRuleViolationException("QRコードの形式が不正です。");
        }
        Map<String, String> queryParameters = parseQueryParameters(uri.getRawQuery());
        String guestId = queryParameters.getOrDefault("guestId", "").trim();
        if (guestId.isEmpty()) {
            throw new CheckInRuleViolationException("QRコードにguestIdが含まれていません。");
        }
        String reservations = queryParameters.getOrDefault("reservations", "");
        Set<String> reservationIds = Arrays.stream(reservations.split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .collect(Collectors.toUnmodifiableSet());

        return new QrCodeData(guestId, reservationIds);
    }

    private Map<String, String> parseQueryParameters(String query) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }
        Map<String, String> parameters = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] tokens = pair.split("=", 2);
            String key = decode(tokens[0]);
            String value = tokens.length > 1 ? decode(tokens[1]) : "";
            parameters.put(key, value);
        }
        return parameters;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private record QrCodeData(String guestId, Set<String> reservations) {}

    public record CheckInResult(
        String guestId,
        CheckInType checkInType,
        String sessionId,
        boolean duplicate,
        Instant checkedInAt
    ) {}

    public record CheckInHistoryItem(
        String guestId,
        CheckInType checkInType,
        String sessionId,
        Instant checkedInAt
    ) {}
}
