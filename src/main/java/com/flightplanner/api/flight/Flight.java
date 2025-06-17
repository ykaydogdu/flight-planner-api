package com.flightplanner.api.flight;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "airline_code", nullable = false, referencedColumnName = "code")
    private Airline airline;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_airport_code", nullable = false, referencedColumnName = "code")
    private Airport sourceAirport;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "destination_airport_code", nullable = false, referencedColumnName = "code")
    private Airport destinationAirport;

    public Flight(final LocalDateTime departureTime, final String airlineCode, final String sourceAirportCode, final String destinationAirportCode) {
        this.departureTime = departureTime;

        this.airline = new Airline();
        this.sourceAirport = new Airport();
        this.destinationAirport = new Airport();

        this.airline.setCode(airlineCode);
        this.sourceAirport.setCode(sourceAirportCode);
        this.destinationAirport.setCode(destinationAirportCode);
    }
}
