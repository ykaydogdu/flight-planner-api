package com.flightplanner.api.auth;

import com.flightplanner.api.auth.dto.AuthResponseDTO;
import com.flightplanner.api.auth.dto.LoginRequestDTO;
import com.flightplanner.api.auth.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {
        User user = authService.registerUser(username, password);
        return new ResponseEntity<>(user.getUsername(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.authenticateAndGetJwt(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    // Test mapping to test auth
    @GetMapping("/protected")
    public ResponseEntity<String> protectedSource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok("Hello from the protected source: " + username);
    }
}
