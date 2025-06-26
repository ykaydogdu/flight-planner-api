package com.flightplanner.api.auth.dto;

import com.flightplanner.api.user.User;
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
        User user = new User(); // Assuming User is a valid class
        AuthResponseDTO authResponse = new AuthResponseDTO("sampleToken", user);

        assertEquals("sampleToken", authResponse.getToken());
        assertEquals(user, authResponse.getUser());
    }

    @Test
    void testAuthResponseDTOSettersAndGetters() {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken("sampleToken");

        assertEquals("sampleToken", authResponse.getToken());
    }
}
