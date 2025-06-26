package com.flightplanner.api.flight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.auth.jwt.JwtAuthenticationFilter;
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
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
import java.util.HashMap;
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
        FlightResponseDTO flight1 = FlightResponseDTO.builder()
                .id(1L)
                .price(100)
                .seatCount(100)
                .emptySeats(50)
                .departureTime(now)
                .duration(120)
                .arrivalTime(now.plusHours(2))
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .build();

        FlightResponseDTO flight2 = FlightResponseDTO.builder()
                .id(2L)
                .price(100)
                .seatCount(100)
                .emptySeats(50)
                .departureTime(now.plusHours(2))
                .duration(120)
                .arrivalTime(now.plusHours(4))
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .build();

        List<FlightResponseDTO> allFlights = Arrays.asList(flight1, flight2);

        // Mock service behavior
        when(flightService.getAllFlights(any(), any(), any(), any())).thenReturn(allFlights);

        // Act & Assert
        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void createFlight_shouldReturnCreatedStatusAndFlightResponseDTO() throws Exception {
        // Arrange
        LocalDateTime departureTime = LocalDateTime.now().plusDays(7);
        FlightRequestDTO requestDTO = new FlightRequestDTO(departureTime, 120, 100.0, 100, "UA", "ORD", "SFO");
        FlightResponseDTO responseDTO = FlightResponseDTO.builder()
                .id(3L)
                .price(100.0)
                .seatCount(100)
                .emptySeats(50)
                .departureTime(departureTime)
                .duration(120)
                .arrivalTime(departureTime.plusHours(2))
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .build();

        // Mock service behavior
        when(flightService.createFlight(any(FlightRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.price", is(100.0)))
                .andExpect(jsonPath("$.seatCount", is(100)));

        // Verify service method was called with appropriate argument
        verify(flightService, times(1)).createFlight(any(FlightRequestDTO.class));
    }

    @Test
    void getFlightById_shouldReturnFlightWhenFound() throws Exception {
        // Arrange
        Long flightId = 4L;
        LocalDateTime now = LocalDateTime.now();
        FlightResponseDTO responseDTO = FlightResponseDTO.builder()
                .id(flightId)
                .price(100.0)
                .seatCount(100)
                .emptySeats(50)
                .departureTime(now)
                .duration(120)
                .arrivalTime(now.plusHours(2))
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .build();

        // Mock service behavior
        when(flightService.getFlightById(flightId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/flights/{id}", flightId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.price", is(100.0)))
                .andExpect(jsonPath("$.seatCount", is(100)));

        // Verify service method was called
        verify(flightService, times(1)).getFlightById(flightId);
    }

    @Test
    void getFlightById_shouldReturnNotFound_whenFlightNotFound() throws Exception {
        // Arrange
        Long flightId = 99L; // Non-existent ID

        // Mock service behavior for not found scenario
        when(flightService.getFlightById(flightId)).thenThrow(new NotFoundException("Flight", new HashMap<>(){{
            put("id", flightId);
        }}));

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
        FlightRequestDTO requestDTO = new FlightRequestDTO(updatedTime, 120, 150.0, 120, "DL", "LAX", "SEA");
        FlightResponseDTO responseDTO = FlightResponseDTO.builder()
                .id(flightId)
                .price(150.0)
                .seatCount(120)
                .emptySeats(60)
                .departureTime(updatedTime)
                .duration(120)
                .arrivalTime(updatedTime.plusHours(2))
                .airline(null) // Mock airline object if needed
                .originAirport(null) // Mock origin airport object if needed
                .destinationAirport(null) // Mock destination airport object if needed
                .build();

        // Mock service behavior
        when(flightService.updateFlight(eq(flightId), any(FlightRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.price", is(150.0)))
                .andExpect(jsonPath("$.seatCount", is(120)));

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