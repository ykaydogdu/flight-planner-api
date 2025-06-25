package com.flightplanner.api.timezone;

import com.flightplanner.api.timezone.dto.TimezoneResponseDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TimezoneService {

    private final RestTemplate restTemplate;
    private static final String TIMEZONE_API_URL = "https://timeapi.io/api/timezone/coordinate";

    public TimezoneService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocalDateTime convertLocalDateTimeToUtc(LocalDateTime localDateTime, TimeZone timezone) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(timezone.toZoneId());
        Instant instant = zonedDateTime.toInstant();
        return instant.atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public LocalDateTime convertUtcToLocalDateTime(LocalDateTime utcDateTime, TimeZone timezone) {
        ZonedDateTime zonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        Instant instant = zonedDateTime.toInstant();
        return instant.atZone(timezone.toZoneId()).toLocalDateTime();
    }

    public TimeZone getTimezone(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromUriString(TIMEZONE_API_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .toUriString();

        TimezoneResponseDTO response = restTemplate.getForObject(url, TimezoneResponseDTO.class);

        if (response != null && response.getTimeZone() != null) {
            String timezone = response.getTimeZone();

            return TimeZone.getTimeZone(timezone);
        } else {
            throw new RuntimeException("Could not retrieve timezone for the given coordinates.");
        }
    }
}
