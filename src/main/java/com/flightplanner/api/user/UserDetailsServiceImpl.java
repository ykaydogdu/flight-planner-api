package com.flightplanner.api.user;

import com.flightplanner.api.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        // Retrieve user
        return userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User", new HashMap<>(){{put("username", username);}}));
    }
}
