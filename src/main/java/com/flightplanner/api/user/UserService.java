package com.flightplanner.api.user;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AirlineRepository airlineRepository;

    public UserService(UserRepository userRepository, AirlineRepository airlineRepository) {
        this.userRepository = userRepository;
        this.airlineRepository = airlineRepository;
    }

    public void assignRoleToUser(String username, String role) {
        User user =  userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", new HashMap<>(){{put("username", username);}}));

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        Role userRole;
        try {
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        user.setRole(userRole);
        userRepository.save(user);
    }

    public void assignAirlineToUser(String username, String airlineCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", new HashMap<>(){{put("username", username);}}));
        if (airlineCode == null || airlineCode.isBlank()) {
            throw new IllegalArgumentException("Airline code cannot be null or empty");
        }

        // Assuming AirlineRepository exists and has a method to find by code
        Airline airline = airlineRepository.findById(airlineCode)
                .orElseThrow(() -> new NotFoundException("Airline"));

        user.setAirline(airline);
        userRepository.save(user);
    }
}
