package com.flightplanner.api.timezone;

import com.flightplanner.api.timezone.dto.TimezoneResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimezoneServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TimezoneService timezoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertLocalDateTimeToUtc() {
        LocalDateTime localDateTime = LocalDateTime.of(2025, 7, 1, 12, 0);
        TimeZone timezone = TimeZone.getTimeZone(ZoneId.of("America/New_York"));

        LocalDateTime result = timezoneService.convertLocalDateTimeToUtc(localDateTime, timezone);

        assertEquals("2025-07-01T16:00", result.toString());
    }

    @Test
    void testConvertUtcToLocalDateTime() {
        LocalDateTime utcDateTime = LocalDateTime.of(2025, 7, 1, 16, 0);
        TimeZone timezone = TimeZone.getTimeZone(ZoneId.of("America/New_York"));

        LocalDateTime result = timezoneService.convertUtcToLocalDateTime(utcDateTime, timezone);

        assertEquals("2025-07-01T12:00", result.toString());
    }

    @Test
    void testGetTimezone() {
        double latitude = 40.7128;
        double longitude = -74.0060;
        String url = "https://timeapi.io/api/timezone/coordinate?latitude=40.7128&longitude=-74.006";

        TimezoneResponseDTO mockResponse = new TimezoneResponseDTO();
        mockResponse.setTimeZone("America/New_York");

        when(restTemplate.getForObject(url, TimezoneResponseDTO.class)).thenReturn(mockResponse);

        TimeZone result = timezoneService.getTimezone(latitude, longitude);

        assertNotNull(result);
        assertEquals("America/New_York", result.getID());
    }
}
