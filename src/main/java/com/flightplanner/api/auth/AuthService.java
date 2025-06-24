package com.flightplanner.api.auth;

import com.flightplanner.api.auth.dto.AuthResponseDTO;
import com.flightplanner.api.auth.jwt.JwtService;
import com.flightplanner.api.user.Role;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import com.flightplanner.api.user.exception.InvalidCredentialsException;
import com.flightplanner.api.user.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(
            final UserRepository userRepository,
            final BCryptPasswordEncoder bCryptPasswordEncoder,
            final JwtService jwtService,
            final AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Tries to create a new user with given credentials.
     * @param username The username to be assigned
     * @param password The plain password
     * @return The created user entity
     */
    public User registerUser(final String username, final String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (userRepository.existsById(username)) {
            throw new UserAlreadyExistsException("Username " + username + " already exists");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, encodedPassword);
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and generates a JWT upon successful authentication.
     * This method uses Spring Security's AuthenticationManager to verify credentials.
     *
     * @param username The username.
     * @param password The plain text password.
     * @return An AuthResponse containing the generated JWT.
     * @throws org.springframework.security.authentication.BadCredentialsException if authentication fails
     * (e.g., incorrect username or password).
     */
    public AuthResponseDTO authenticateAndGetJwt(String username, String password) {
        // authenticate the user
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // retrieve user details
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            // fetch user entity from repository
            User user = userRepository.findById(userDetails.getUsername())
                    .orElseThrow(InvalidCredentialsException::new);

            // generate token
            String jwt = jwtService.generateToken(userDetails);

            return new AuthResponseDTO(jwt, user);
        } catch (RuntimeException ex) {
            throw new InvalidCredentialsException();
        }
    }

}
