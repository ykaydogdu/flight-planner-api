package com.flightplanner.api.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignRoleToUser() {
        String username = "testUser";
        String role = "ADMIN";

        doNothing().when(userService).assignRoleToUser(username, role);

        ResponseEntity<String> response = userController.assignRoleToUser(username, role);

        assertEquals(ResponseEntity.ok("Role " + role + " assigned to user " + username), response);
        verify(userService, times(1)).assignRoleToUser(username, role);
    }

    @Test
    void testAssignAirlineToUser() {
        String username = "testUser";
        String airlineCode = "AA123";

        doNothing().when(userService).assignAirlineToUser(username, airlineCode);

        ResponseEntity<String> response = userController.assignAirlineToUser(username, airlineCode);

        assertEquals(ResponseEntity.ok("Airline " + airlineCode + " assigned to user " + username), response);
        verify(userService, times(1)).assignAirlineToUser(username, airlineCode);
    }
}
