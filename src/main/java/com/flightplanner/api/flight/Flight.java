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
    private Long id;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "seat_count", nullable = false)
    private int seatCount;

    @ManyToOne
    @JoinColumn(name = "airline_code", nullable = false, referencedColumnName = "code")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "origin_airport_code", nullable = false, referencedColumnName = "code")
    private Airport originAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_code", nullable = false, referencedColumnName = "code")
    private Airport destinationAirport;

    public Flight(final LocalDateTime departureTime,
                  final double price,
                  final int seatCount,
                  final Airline airline,
                  final Airport originAirport,
                  final Airport destinationAirport) {
        if (originAirport == destinationAirport) {
            throw new IllegalArgumentException("Source and destination Airport are the same");
        }

        this.departureTime = departureTime;
        this.price = price;
        this.seatCount = seatCount;

        this.airline = airline;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
    }

    public String getAirlineCode() {
        return this.airline != null ? this.airline.getCode() : null;
    }
}
