package com.flightplanner.api.airline;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Airline {

    @Id
    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

}
