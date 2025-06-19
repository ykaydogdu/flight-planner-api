package com.flightplanner.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightplanner.api.auth.dto.AuthRequestDTO;
import com.flightplanner.api.auth.dto.AuthResponseDTO;
import com.flightplanner.api.auth.jwt.JwtAuthenticationFilter;
import com.flightplanner.api.auth.user.User;
import com.flightplanner.api.auth.user.UserRepository;
import com.flightplanner.api.auth.user.exception.InvalidCredentialsException;
import com.flightplanner.api.auth.user.exception.UserAlreadyExistsException;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
// Import for Spring Security Test utilities
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable filters to test without JWT
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private Server h2Server;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityContext originalContext;

    @BeforeEach
    void saveContext() {
        // save the original security context
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void restoreContext() {
        // restore the original security context
        SecurityContextHolder.setContext(originalContext);
    }

    @Test
    void registerUser_success() throws Exception {
        // Arrange
        User usr = new User("testuser", "testpwd");
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "testpwd");

        // Act
        when(authService.registerUser(requestDTO.getUsername(), requestDTO.getPassword())).thenReturn(usr);

        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(usr.getUsername()));
    }

    @Test
    void registerUser_duplicate() throws Exception {
        User usr = new User("testuser", "testpwd");
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "testpwd");

        when(authService.registerUser(requestDTO.getUsername(), requestDTO.getPassword())).thenReturn(usr);

        mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(usr.getUsername()));

        when(authService.registerUser(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new UserAlreadyExistsException(requestDTO.getUsername()));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginUser_success() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "testpwd");
        AuthResponseDTO responseDTO = new AuthResponseDTO("testtoken");

        when(authService.authenticateAndGetJwt(requestDTO.getUsername(), requestDTO.getPassword())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(responseDTO.getToken())));
    }

    @Test
    void loginUser_shouldReturnNotFoundWhenWrongCredidentials() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "wrongpwd");

        when(authService.authenticateAndGetJwt(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenMissingFields() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO(null, "testpwd");

        when(authService.registerUser(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new IllegalArgumentException("Username cannot be null or empty"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        requestDTO = new AuthRequestDTO("testuser", null);
        when(authService.registerUser(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new IllegalArgumentException("Password cannot be null or empty"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenMissingFields() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO(null, "testpwd");

        when(authService.authenticateAndGetJwt(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new IllegalArgumentException("Username cannot be null or empty"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        requestDTO = new AuthRequestDTO("testuser", null);

        when(authService.authenticateAndGetJwt(requestDTO.getUsername(), requestDTO.getPassword()))
                .thenThrow(new IllegalArgumentException("Password cannot be null or empty"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedSource_shouldReturnGreetingForAuthenticatedUser() throws Exception {
        String username = "testuser";

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/v1/auth/protected"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from the protected source: " + username));
    }

    @Test
    void protectedSource_shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        SecurityContext emptyContext = mock(SecurityContext.class);
        when(emptyContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(emptyContext);

        mockMvc.perform(get("/api/v1/auth/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_shouldReturnUserDetailsForAuthenticatedUser() throws Exception {
        String username = "testuser";
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User usr = new User(username, "testpwd");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(usr));

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void me_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        String username = "nonexistentuser";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isNotFound());
    }

}
