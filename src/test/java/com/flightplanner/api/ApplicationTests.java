package com.flightplanner.api;


import com.flightplanner.api.auth.jwt.JwtAuthenticationFilter;
import com.flightplanner.api.auth.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//TODO: Fix the test to return 201 Created for user registration
@SpringBootTest
@AutoConfigureMockMvc // Disable security filters for testing
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnOkForPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/flights/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRegisterAndLoginUser() throws Exception {
        // Register a new user
        String registerPayload = "{" +
                "\"username\": \"integrationuser\"," +
                "\"password\": \"integrationpass\"}";
        mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload)
        ).andExpect(status().isCreated()); //TODO: Fix this to return 201 Created

        // Login with the new user
        String loginPayload = "{" +
                "\"username\": \"integrationuser\"," +
                "\"password\": \"integrationpass\"}";
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.token").exists());
    }
}
