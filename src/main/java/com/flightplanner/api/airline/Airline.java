package com.flightplanner.api.airline;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airline {

    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(name = "name")
    private String name;

}
