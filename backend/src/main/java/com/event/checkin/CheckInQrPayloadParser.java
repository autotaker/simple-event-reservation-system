package com.event.checkin;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CheckInQrPayloadParser {

    private static final String QR_SCHEME = "event-reservation";
    private static final String QR_HOST = "checkin";

    public CheckInQrCodeData parse(String qrCodePayload) {
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

        return new CheckInQrCodeData(guestId, reservationIds);
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

    public record CheckInQrCodeData(String guestId, Set<String> reservations) {}
}
