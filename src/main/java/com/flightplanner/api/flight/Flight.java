package com.flightplanner.api.flight;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;

    @Getter
    @Setter
    private LocalDateTime departureTime;

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

    String getAirlineCode() {
        return airline.getCode();
    }

    String getSrcAirportCode() {
        return srcAirport.getCode();
    }

    String getDestAirportCode() {
        return destAirport.getCode();
    }
}
