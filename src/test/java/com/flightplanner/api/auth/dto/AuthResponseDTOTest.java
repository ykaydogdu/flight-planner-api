package com.flightplanner.api.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDTOTest {

    @Test
    void testAuthResponseDTOBuilder() {
        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .token("sampleToken")
                .build();

        assertEquals("sampleToken", authResponse.getToken());
    }

    @Test
    void testAuthResponseDTOConstructor() {
        AuthResponseDTO authResponse = new AuthResponseDTO("sampleToken");

        assertEquals("sampleToken", authResponse.getToken());
    }

    @Test
    void testAuthResponseDTOSettersAndGetters() {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken("sampleToken");

        assertEquals("sampleToken", authResponse.getToken());
    }
}
