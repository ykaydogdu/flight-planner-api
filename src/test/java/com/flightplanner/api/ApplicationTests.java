package com.flightplanner.api; // Adjust package to match your main Application.java

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.airport.AirportRepository;
import com.flightplanner.api.flight.Flight; // Assuming Flight entity is used
import com.flightplanner.api.flight.FlightRepository;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


// @SpringBootTest loads the full Spring application context
// webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT starts the embedded server on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // Configures MockMvc for web layer testing with the full context
@ActiveProfiles("test") // Activates the 'test' profile (e.g., for H2 database config)
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests against the running application

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private FlightRepository flightRepository; // Use the real repository to interact with the test DB

    @Autowired
    private ObjectMapper objectMapper; // For JSON conversion

    @BeforeEach
    void setUp() {
        // Ensure a clean state before each test
        flightRepository.deleteAll();
        airlineRepository.deleteAll();
        airportRepository.deleteAll();

        // Insert mock airline and airport data
        airlineRepository.save(new Airline("TK", "Turkish Airlines"));
        airportRepository.save(new Airport("SAW", "Sabiha Gokcen Airport"));
        airportRepository.save(new Airport("ESB", "Esenboga Airport"));

        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * This is a basic "smoke test" to ensure that the Spring application context loads without errors.
     * If the context fails to load (e.g., due to circular dependencies, missing beans, config issues),
     * this test will fail.
     */
    @Test
    void contextLoads() {
        // If the application context loads successfully, this test passes.
        // No explicit assertions are needed here.
        assertTrue(true); // Just a placeholder, context loading itself is the test
    }

    /**
     * This test verifies an end-to-end scenario:
     * - A POST request creates a flight (using controller, service, and saving to real DB).
     * - A GET request retrieves the created flight (using controller, service, and querying real DB).
     */
    @Test
    void createAndRetrieveFlightIntegrationTest() throws Exception {
        // Arrange
        LocalDateTime departureTime = LocalDateTime.now().plusDays(5);
        FlightRequestDTO requestDTO = new FlightRequestDTO(departureTime, "TK", "SAW", "ESB");

        String requestJson = objectMapper.writeValueAsString(requestDTO);

        // Perform POST request to create a flight
        mockMvc.perform(post("/api/v1/flights/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.id").exists()) // Check that an ID is generated
                .andExpect(jsonPath("$.airlineCode", is("TK")))
                .andExpect(jsonPath("$.srcAirportCode", is("SAW")));

        // Act & Assert
        // After creation, we expect one flight in the database
        List<Flight> allFlights = flightRepository.findAll();
        // Since we are using FlightResponseDTO, the id is mapped after save.
        // It's more robust to fetch the ID from the response if possible,
        // or just rely on findAll().
        Long createdFlightId = allFlights.getFirst().getId(); // Get the ID of the first (and only) flight

        mockMvc.perform(get("/api/v1/flights/{id}", createdFlightId))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(createdFlightId.intValue()))) // jsonPath reads as int
                .andExpect(jsonPath("$.airlineCode", is("TK")))
                .andExpect(jsonPath("$.srcAirportCode", is("SAW")))
                .andExpect(jsonPath("$.destAirportCode", is("ESB")));

        // Verify that the flight exists in the database
        assertTrue(flightRepository.findById(createdFlightId).isPresent());
    }

    /**
     * Test the GET all flights endpoint for basic functionality.
     * It should return an empty list initially.
     */
    @Test
    void getAllFlights_shouldReturnEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/v1/flights/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0))); // Expect an empty array
    }
}