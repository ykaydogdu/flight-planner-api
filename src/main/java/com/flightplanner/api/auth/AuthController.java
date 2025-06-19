package com.flightplanner.api.auth;

import com.flightplanner.api.auth.dto.AuthResponseDTO;
import com.flightplanner.api.auth.dto.AuthRequestDTO;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(final AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequestDTO registerRequest) {
        User user = authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
        return new ResponseEntity<>(user.getUsername(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody AuthRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.authenticateAndGetJwt(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    // Test mapping to test auth
    @GetMapping("/protected")
    public ResponseEntity<String> protectedSource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("Hello from the protected source: " + username);
    }

    @GetMapping("/me")
    public ResponseEntity<User> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User usr = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not founds"));
        return ResponseEntity.ok(usr);
    }

    @PatchMapping("/{username}/assign-role")
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username, @RequestParam String role) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        authService.assignRoleToUser(user, role);
        return ResponseEntity.ok("Role " + role + " assigned to user " + username);
    }


    @PatchMapping("/assign-airline")
    public ResponseEntity<String> assignAirlineToUser(@RequestParam String username, @RequestParam String airlineCode) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        authService.assignAirlineToUser(user, airlineCode);
        return ResponseEntity.ok("Airline " + airlineCode + " assigned to user " + username);
    }

}
