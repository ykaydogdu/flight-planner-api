package com.flightplanner.api.booking;

import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingResponseDTO bookingResponseDTO;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setFlightId(1L);
        bookingRequestDTO.setUsername("username");

        bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setId(1L);
        bookingResponseDTO.setFlightId(1L);
        bookingResponseDTO.setUsername("username");

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(bookingResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flightId\":1,\"username\":\"user1\",\"numberOfSeats\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.flightId").value(1));

    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(bookingResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.flightId").value(1));
    }

    @Test
    void testDeleteBooking() throws Exception {
        Mockito.doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/1"))
                .andExpect(status().isNoContent());
    }
}
