package com.flightplanner.api.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthRequestDTOTest {

    @Test
    void testAuthRequestDTOBuilder() {
        AuthRequestDTO authRequest = AuthRequestDTO.builder()
                .username("testuser")
                .password("testpassword")
                .build();

        assertEquals("testuser", authRequest.getUsername());
        assertEquals("testpassword", authRequest.getPassword());
    }

    @Test
    void testAuthRequestDTOConstructor() {
        AuthRequestDTO authRequest = new AuthRequestDTO("testuser", "testpassword");

        assertEquals("testuser", authRequest.getUsername());
        assertEquals("testpassword", authRequest.getPassword());
    }

    @Test
    void testAuthRequestDTOSettersAndGetters() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpassword");

        assertEquals("testuser", authRequest.getUsername());
        assertEquals("testpassword", authRequest.getPassword());
    }
}
