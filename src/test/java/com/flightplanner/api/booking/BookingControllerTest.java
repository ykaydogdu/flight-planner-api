package com.flightplanner.api.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightplanner.api.auth.jwt.JwtService;
import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponseDTO bookingResponseDTO;
    private BookingRequestDTO bookingRequestDTO;

    @BeforeEach
    void setUp() {
        bookingRequestDTO = BookingRequestDTO.builder()
                .flightId(1L)
                .username("testuser")
                .numberOfSeats(2)
                .build();

        bookingResponseDTO = BookingResponseDTO.builder()
                .id(1L)
                .flightId(1L)
                .price(250.00)
                .numberOfSeats(2)
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(bookingResponseDTO);

        mockMvc.perform(post("/api/v1/bookings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.flightId", is(1)))
                .andExpect(jsonPath("$.numberOfSeats", is(2)));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong())).thenReturn(bookingResponseDTO);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.flightId", is(1)));
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
