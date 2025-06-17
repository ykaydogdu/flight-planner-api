package com.flightplanner.api.flight;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;

    @OneToOne
    @Getter
    @Setter
    private Airline airline;

    @OneToOne
    @Getter
    @Setter
    private Airport srcAirport;

    @OneToOne
    @Getter
    @Setter
    private Airport destAirport;
}
