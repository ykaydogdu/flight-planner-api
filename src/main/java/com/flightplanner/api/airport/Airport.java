package com.flightplanner.api.airport;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Airport(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
