package com.flightplanner.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{username}/assign-role")
    @Operation(summary = "Assign a role to a user", description = "Assigns a specified role to a user by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username, @RequestParam String role) {
        userService.assignRoleToUser(username, role);
        return ResponseEntity.ok("Role " + role + " assigned to user " + username);
    }


    @PatchMapping("/{username}/assign-airline")
    @Operation(summary = "Assign an airline to a user", description = "Assigns a specified airline to a user by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Airline assigned successfully"),
            @ApiResponse(responseCode = "404", description = "User or airline not found"),
    })
    public ResponseEntity<String> assignAirlineToUser(@PathVariable String username, @RequestParam String airlineCode) {
        userService.assignAirlineToUser(username, airlineCode);
        return ResponseEntity.ok("Airline " + airlineCode + " assigned to user " + username);
    }
}
