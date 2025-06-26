package com.flightplanner.api.airline;

import com.flightplanner.api.airline.dto.AirlineWithStaffCountDTO;
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

class AirlineControllerTest {

    @Mock
    private AirlineService airlineService;

    @InjectMocks
    private AirlineController airlineController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(airlineController).build();
    }

    @Test
    void testGetAllAirlines() throws Exception {
        List<AirlineWithStaffCountDTO> airlines = Arrays.asList(
            new AirlineWithStaffCountDTO("AA", "American Airlines", 100),
            new AirlineWithStaffCountDTO("DL", "Delta Airlines", 200)
        );

        when(airlineService.getAllAirlines()).thenReturn(airlines);

        mockMvc.perform(get("/api/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("AA"))
                .andExpect(jsonPath("$[0].name").value("American Airlines"))
                .andExpect(jsonPath("$[0].staffCount").value(100))
                .andExpect(jsonPath("$[1].code").value("DL"))
                .andExpect(jsonPath("$[1].name").value("Delta Airlines"))
                .andExpect(jsonPath("$[1].staffCount").value(200));

        verify(airlineService).getAllAirlines();
    }

    @Test
    void testAddAirline() throws Exception {
        AirlineWithStaffCountDTO airline = new AirlineWithStaffCountDTO("AA", "American Airlines", 100);

        when(airlineService.addAirline(any(Airline.class))).thenReturn(airline);

        mockMvc.perform(post("/api/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"AA\",\"name\":\"American Airlines\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("AA"))
                .andExpect(jsonPath("$.name").value("American Airlines"))
                .andExpect(jsonPath("$.staffCount").value(100));

        verify(airlineService).addAirline(any(Airline.class));
    }

    @Test
    void testGetAirlineByCode() throws Exception {
        AirlineWithStaffCountDTO airline = new AirlineWithStaffCountDTO("AA", "American Airlines", 100);

        when(airlineService.getAirlineByCode("AA")).thenReturn(airline);

        mockMvc.perform(get("/api/v1/airlines/AA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("AA"))
                .andExpect(jsonPath("$.name").value("American Airlines"))
                .andExpect(jsonPath("$.staffCount").value(100));

        verify(airlineService).getAirlineByCode("AA");
    }

    @Test
    void testDeleteAirline() throws Exception {
        doNothing().when(airlineService).deleteAirline("AA");

        mockMvc.perform(delete("/api/v1/airlines/AA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(airlineService).deleteAirline("AA");
    }
}
