package com.flightplanner.api.user;

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
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username, @RequestParam String role) {
        userService.assignRoleToUser(username, role);
        return ResponseEntity.ok("Role " + role + " assigned to user " + username);
    }


    @PatchMapping("/{username}/assign-airline")
    public ResponseEntity<String> assignAirlineToUser(@PathVariable String username, @RequestParam String airlineCode) {
        userService.assignAirlineToUser(username, airlineCode);
        return ResponseEntity.ok("Airline " + airlineCode + " assigned to user " + username);
    }
}
