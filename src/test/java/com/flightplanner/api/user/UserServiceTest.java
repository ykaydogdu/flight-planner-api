package com.flightplanner.api.user;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airline.Airline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignRoleToUser() {
        String username = "testUser";
        String role = "ROLE_ADMIN";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        userService.assignRoleToUser(username, role);

        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAssignRoleToUser_UserNotFound() {
        String username = "nonExistentUser";
        String role = "ADMIN";

        when(userRepository.findById(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.assignRoleToUser(username, role));

        assertEquals("User not found with parameters: username=" + username, exception.getMessage());
    }

    @Test
    void testAssignRoleToUser_InvalidRole() {
        String username = "testUser";
        String role = "INVALID_ROLE";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.assignRoleToUser(username, role));

        assertEquals("Invalid role: " + role, exception.getMessage());
    }

    @Test
    void testAssignRoleToUser_NullRole() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.assignRoleToUser(username, null));

        assertEquals("Role cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAssignRoleToUser_EmptyRole() {
        String username = "testUser";
        String role = "";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.assignRoleToUser(username, role));

        assertEquals("Role cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAssignRoleToUser_ValidRoleLowerCase() {
        String username = "testUser";
        String role = "role_admin";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        userService.assignRoleToUser(username, role);

        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAssignRoleToUser_ValidRoleMixedCase() {
        String username = "testUser";
        String role = "RoLe_AdMiN";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        userService.assignRoleToUser(username, role);

        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAssignAirlineToUser() {
        String username = "testUser";
        String airlineCode = "AA123";
        User user = new User();
        user.setUsername(username);
        Airline airline = new Airline();
        airline.setCode(airlineCode);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(airlineRepository.findById(airlineCode)).thenReturn(Optional.of(airline));

        userService.assignAirlineToUser(username, airlineCode);

        assertEquals(airline, user.getAirline());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAssignAirlineToUser_UserNotFound() {
        String username = "nonExistentUser";
        String airlineCode = "AA123";

        when(userRepository.findById(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.assignAirlineToUser(username, airlineCode));

        assertEquals("User not found with parameters: username=" + username, exception.getMessage());
    }

    @Test
    void testAssignAirlineToUser_NullAirlineCode() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.assignAirlineToUser(username, null));

        assertEquals("Airline code cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAssignAirlineToUser_EmptyAirlineCode() {
        String username = "testUser";
        String airlineCode = "";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.assignAirlineToUser(username, airlineCode));

        assertEquals("Airline code cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAssignAirlineToUser_AirlineNotFound() {
        String username = "testUser";
        String airlineCode = "INVALID_CODE";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(airlineRepository.findById(airlineCode)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.assignAirlineToUser(username, airlineCode));

        assertEquals("Airline not found", exception.getMessage());
    }

    @Test
    void testAssignAirlineToUser_ValidAirlineCode() {
        String username = "testUser";
        String airlineCode = "AA123";
        User user = new User();
        user.setUsername(username);
        Airline airline = new Airline();
        airline.setCode(airlineCode);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(airlineRepository.findById(airlineCode)).thenReturn(Optional.of(airline));

        userService.assignAirlineToUser(username, airlineCode);

        assertEquals(airline, user.getAirline());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findById(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.getUserByUsername(username));

        assertEquals("User not found with parameters: username=" + username, exception.getMessage());
    }
}
