package com.flightplanner.api.auth.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        byte[] rawKey = "0123456789ABCDEF0123456789ABCDEF".getBytes();
        String b64Key = Base64.getEncoder().encodeToString(rawKey);

        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", b64Key, String.class);
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION_MS", 86400000, int.class); // 24 hours
    }

    @Test
    void testExtractUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testIsTokenValid() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testExtractClaim() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void testExtractAllClaimsWithInvalidToken() {
        String invalidToken = "invalidToken";

        assertThrows(Exception.class, () -> jwtService.extractAllClaims(invalidToken));
    }
}
