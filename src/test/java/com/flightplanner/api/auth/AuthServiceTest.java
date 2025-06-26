package com.flightplanner.api.auth;

import com.flightplanner.api.auth.dto.RegisterRequestDTO;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import com.flightplanner.api.user.Role;
import com.flightplanner.api.user.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        String username = "testuser";
        String password = "testpassword";
        String encodedPassword = "encodedPassword";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(username, password, "Test", "User", "test@example.com");

        when(userRepository.existsById(username)).thenReturn(false);
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.registerUser(registerRequest);

        assertEquals(username, user.getUsername());
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(Role.ROLE_USER, user.getRole());

        verify(userRepository).existsById(username);
        verify(bCryptPasswordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserThrowsExceptionWhenUsernameExists() {
        String username = "testuser";
        String password = "testpassword";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(username, password, "Test", "User", "test@example.com");

        when(userRepository.existsById(username)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(registerRequest));

        verify(userRepository).existsById(username);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void testRegisterUserThrowsExceptionWhenUsernameIsNull() {
        String password = "testpassword";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(null, password, "Test", "User", "test@example.com");

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(registerRequest));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void testRegisterUserThrowsExceptionWhenPasswordIsNull() {
        String username = "testuser";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(username, null, "Test", "User", "test@example.com");

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(registerRequest));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(authenticationManager);
    }
}
