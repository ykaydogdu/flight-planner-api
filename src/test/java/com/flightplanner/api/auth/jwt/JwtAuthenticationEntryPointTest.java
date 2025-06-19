package com.flightplanner.api.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    }

    @Test
    void testCommenceSetsUnauthorizedStatusAndMessage() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);
        when(authException.getMessage()).thenReturn("Unauthorized access");

        jwtAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).println("{\"message\":\"Unauthorized access\"}");
    }

    @Test
    void testCommenceHandlesIOException() throws Exception {
        when(response.getWriter()).thenThrow(new IOException("Error writing response"));
        when(authException.getMessage()).thenReturn("Unauthorized access");

        assertDoesNotThrow(() -> jwtAuthenticationEntryPoint.commence(request, response, authException));

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
