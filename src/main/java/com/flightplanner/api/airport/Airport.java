package com.flightplanner.api.airport;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Airport {

    @Id
    private String code;
    private String name;
}
