package com.flightplanner.api.flight;

//import com.flightplanner.api.airline.Airline;
//import com.flightplanner.api.airport.Airport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "airline_code", nullable = false)
    private String airlineCode;

    @Column(name = "source_airport_code", nullable = false)
    private String srcAirportCode;

    @Column(name = "destination_airport_code", nullable = false)
    private String destAirportCode;

    public Flight() {}

    public Flight(LocalDateTime departureTime, String airlineCode, String srcAirportCode, String destAirportCode) {
        this.departureTime = departureTime;
        this.airlineCode = airlineCode;
        this.srcAirportCode = srcAirportCode;
        this.destAirportCode = destAirportCode;
    }
}
