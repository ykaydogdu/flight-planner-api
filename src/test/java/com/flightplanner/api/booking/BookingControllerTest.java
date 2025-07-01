package com.flightplanner.api.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightplanner.api.auth.jwt.JwtAuthenticationFilter;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import com.flightplanner.api.booking.dto.BookingPassengerResponseDTO;
import com.flightplanner.api.flight.classes.FlightClassEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponseDTO bookingResponseDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        // Create passenger response DTOs
        BookingPassengerResponseDTO passengerResponse = BookingPassengerResponseDTO.builder()
                .passengerId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .flightClass(FlightClassEnum.ECONOMY)
                .priceAtBooking(100.0)
                .build();
        
        List<BookingPassengerResponseDTO> passengerResponses = Arrays.asList(passengerResponse);

        bookingResponseDTO = BookingResponseDTO.builder()
                .id(1L)
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .departureTime(LocalDateTime.now().plusDays(1))
                .flightDuration(120)
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .passengers(passengerResponses)
                .build();
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong())).thenReturn(bookingResponseDTO);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.flightDuration", is(120)));
    }

    @Test
    void shouldDeleteBooking() throws Exception {
        doNothing().when(bookingService).deleteBooking(anyLong());

        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetMyBookings() throws Exception {
        List<BookingResponseDTO> bookings = Collections.singletonList(bookingResponseDTO);
        when(bookingService.getMyBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
