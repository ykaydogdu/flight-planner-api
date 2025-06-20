package com.flightplanner.api.auth;

import com.flightplanner.api.auth.dto.AuthResponseDTO;
import com.flightplanner.api.auth.dto.AuthRequestDTO;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Register a new user", description = "Registers a new user with a username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<String> registerUser(@RequestBody AuthRequestDTO registerRequest) {
        User user = authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
        return new ResponseEntity<>(user.getUsername(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody AuthRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.authenticateAndGetJwt(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    // Test mapping to test auth
    @GetMapping("/protected")
    @Operation(summary = "Access protected resource", description = "Access a protected resource that requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessed protected resource successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<String> protectedSource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("Hello from the protected source: " + username);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user details", description = "Retrieves the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User usr = userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not founds"));
        return ResponseEntity.ok(usr);
    }
}
