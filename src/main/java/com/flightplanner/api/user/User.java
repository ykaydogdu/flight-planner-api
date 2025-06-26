package com.flightplanner.api.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flightplanner.api.airline.Airline;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "app_user")
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JoinColumn(name = "airline_code", referencedColumnName = "code")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Airline airline;


    public User(String username, String hashedPassword) {
        this.username = username;
        this.password = hashedPassword;
        this.role = Role.ROLE_USER;
    }

    public User(String username, String hashedPassword, String firstName, String lastName, String email) {
        // Email format check
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.username = username;
        this.password = hashedPassword;
        this.email = email;
        this.role = Role.ROLE_USER;
    }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }
}
