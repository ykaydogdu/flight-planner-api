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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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
    @JoinColumn(name = "src_airport_code", nullable = false, referencedColumnName = "code")
    private Airport srcAirport;

    @ManyToOne
    @JoinColumn(name = "dest_airport_code", nullable = false, referencedColumnName = "code")
    private Airport destAirport;

    public Flight(final LocalDateTime departureTime, final Airline airline, final Airport srcAirport, final Airport destAirport) {
        if (srcAirport == destAirport) {
            throw new IllegalArgumentException("Source and destination Airport are the same");
        }

        this.departureTime = departureTime;

        this.airline = airline;
        this.srcAirport = srcAirport;
        this.destAirport = destAirport;
    }

    public String getAirlineCode() {
        return this.airline != null ? this.airline.getCode() : null;
    }
}
