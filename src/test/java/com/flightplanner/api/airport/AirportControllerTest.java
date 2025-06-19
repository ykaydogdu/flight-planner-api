package com.flightplanner.api.airport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AirportControllerTest {

    @Mock
    private AirportService airportService;

    @InjectMocks
    private AirportController airportController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(airportController).build();
    }

    @Test
    void testGetAllAirports() throws Exception {
        List<Airport> airports = Arrays.asList(new Airport("JFK", "John F. Kennedy International Airport"),
                                               new Airport("LAX", "Los Angeles International Airport"));

        when(airportService.getAllAirports()).thenReturn(airports);

        mockMvc.perform(get("/api/v1/airports/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("JFK"))
                .andExpect(jsonPath("$[0].name").value("John F. Kennedy International Airport"))
                .andExpect(jsonPath("$[1].code").value("LAX"))
                .andExpect(jsonPath("$[1].name").value("Los Angeles International Airport"));

        verify(airportService).getAllAirports();
    }

    @Test
    void testCreateAirport() throws Exception {
        Airport airport = new Airport("JFK", "John F. Kennedy International Airport");

        when(airportService.createAirport(any(Airport.class))).thenReturn(airport);

        mockMvc.perform(post("/api/v1/airports/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"JFK\",\"name\":\"John F. Kennedy International Airport\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("JFK"))
                .andExpect(jsonPath("$.name").value("John F. Kennedy International Airport"));

        verify(airportService).createAirport(any(Airport.class));
    }

    @Test
    void testGetAirportByCode() throws Exception {
        Airport airport = new Airport("JFK", "John F. Kennedy International Airport");

        when(airportService.getAirportByCode("JFK")).thenReturn(airport);

        mockMvc.perform(get("/api/v1/airports/JFK")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("JFK"))
                .andExpect(jsonPath("$.name").value("John F. Kennedy International Airport"));

        verify(airportService).getAirportByCode("JFK");
    }

    @Test
    void testDeleteAirport() throws Exception {
        doNothing().when(airportService).deleteAirport("JFK");

        mockMvc.perform(delete("/api/v1/airports/JFK")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(airportService).deleteAirport("JFK");
    }
}
