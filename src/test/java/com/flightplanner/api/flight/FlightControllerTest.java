package com.flightplanner.api.flight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightplanner.api.auth.jwt.JwtAuthenticationFilter;
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightService flightService;
    @MockitoBean
    private FlightMapper flightMapper;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllFlights_shouldReturnListOfFlights() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FlightResponseDTO flight1 = new FlightResponseDTO(1L, 100, 100, now, "THY", "IST", "SAW");
        FlightResponseDTO flight2 = new FlightResponseDTO(2L, 100, 100, now.plusHours(2), "DL", "IST", "SAW");
        List<FlightResponseDTO> allFlights = Arrays.asList(flight1, flight2);

        // Mock service behavior
        when(flightService.getAllFlights()).thenReturn(allFlights);

        // Assert
        mockMvc.perform(get("/api/v1/flights/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].airlineCode", is("THY")))
                .andExpect(jsonPath("$[0].srcAirportCode", is("IST")))
                .andExpect(jsonPath("$[0].destAirportCode", is("SAW")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].airlineCode", is("DL")))
                .andExpect(jsonPath("$[1].srcAirportCode", is("IST")))
                .andExpect(jsonPath("$[1].destAirportCode", is("SAW")));

        // verify service method is called
        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    void createFlight_shouldReturnCreatedStatusAndFlightResponseDTO() throws Exception {
        // Arrange
        LocalDateTime departureTime = LocalDateTime.now().plusDays(7);
        FlightRequestDTO requestDTO = new FlightRequestDTO(departureTime, 100, 100, "UA", "ORD", "SFO");
        FlightResponseDTO responseDTO = new FlightResponseDTO(3L, 100, 100, departureTime, "UA", "ORD", "SFO");

        // Mock service behavior
        when(flightService.createFlight(any(FlightRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/flights/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.airlineCode", is("UA")))
                .andExpect(jsonPath("$.srcAirportCode", is("ORD")));

        // Verify service method was called with appropriate argument
        verify(flightService, times(1)).createFlight(any(FlightRequestDTO.class));
    }

    @Test
    void getFlightById_shouldReturnFlightWhenFound() throws Exception {
        // Arrange
        Long flightId = 4L;
        LocalDateTime now = LocalDateTime.now();
        FlightResponseDTO responseDTO = new FlightResponseDTO(flightId, 100, 100, now, "AA", "DFW", "MIA");

        // Mock service behavior
        when(flightService.getFlightById(flightId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/flights/{id}", flightId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.airlineCode", is("AA")))
                .andExpect(jsonPath("$.srcAirportCode", is("DFW")))
                .andExpect(jsonPath("$.destAirportCode", is("MIA")));

        // Verify service method was called
        verify(flightService, times(1)).getFlightById(flightId);
    }

    @Test
    void getFlightById_shouldReturnNotFound_whenFlightNotFound() throws Exception {
        // Arrange
        Long flightId = 99L; // Non-existent ID

        // Mock service behavior for not found scenario
        when(flightService.getFlightById(flightId)).thenThrow(new FlightNotFoundException(flightId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/flights/{id}", flightId))
                .andExpect(status().isNotFound());

        // Verify service method was called
        verify(flightService, times(1)).getFlightById(flightId);
    }

    @Test
    void updateFlight_shouldReturnOkStatusAndUpdatedFlightResponseDTO() throws Exception {
        // Arrange
        Long flightId = 5L;
        LocalDateTime updatedTime = LocalDateTime.now().plusDays(10);
        FlightRequestDTO requestDTO = new FlightRequestDTO(updatedTime, 100, 100, "DL", "LAX", "SEA");
        FlightResponseDTO responseDTO = new FlightResponseDTO(flightId, 100, 100, updatedTime, "DL", "LAX", "SEA");

        // Mock service behavior
        when(flightService.updateFlight(eq(flightId), any(FlightRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.airlineCode", is("DL")))
                .andExpect(jsonPath("$.srcAirportCode", is("LAX")));

        // Verify service method was called
        verify(flightService, times(1)).updateFlight(eq(flightId), any(FlightRequestDTO.class));
    }

    @Test
    void deleteFlight_shouldReturnNoContentStatus() throws Exception {
        // Arrange
        Long flightId = 6L;

        // Mock service behavior: just do nothing when deleteFlight is called
        doNothing().when(flightService).deleteFlight(flightId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/flights/{id}", flightId))
                .andExpect(status().isNoContent());

        // Verify service method was called
        verify(flightService, times(1)).deleteFlight(flightId);
    }
}