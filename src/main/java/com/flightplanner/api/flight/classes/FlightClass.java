package com.flightplanner.api.flight.classes;

import com.flightplanner.api.flight.Flight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(FlightClassId.class)
public class FlightClass {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Flight flight;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "flight_class", nullable = false)
    private FlightClassEnum flightClass;

    @Column(name = "seat_count", nullable = false)
    private int seatCount;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "price", nullable = false)
    private double price;

    public FlightClass(Flight flight, FlightClassEnum flightClass, int seatCount, double price) {
        this.flight = flight;
        this.flightClass = flightClass;
        this.seatCount = seatCount;
        this.availableSeats = seatCount; // Initially all seats are available
        this.price = price;
    }
}
