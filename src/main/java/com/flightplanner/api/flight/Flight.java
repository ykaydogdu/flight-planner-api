package com.flightplanner.api.flight;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.booking.passenger.BookingPassenger;
import com.flightplanner.api.flight.classes.FlightClass;
import com.flightplanner.api.flight.dto.FlightClassSeatCountDTO;
import com.flightplanner.api.NotFoundException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "duration", nullable = false)
    private int duration;

    @ManyToOne
    @JoinColumn(name = "airline_code", nullable = false, referencedColumnName = "code")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "origin_airport_code", nullable = false, referencedColumnName = "code")
    private Airport originAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_code", nullable = false, referencedColumnName = "code")
    private Airport destinationAirport;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightClass> classes = new ArrayList<>();

    public Flight(final LocalDateTime departureTime,
                  final int duration,
                  final LocalDateTime arrivalTime,
                  final Airline airline,
                  final Airport originAirport,
                  final Airport destinationAirport,
                  final List<FlightClass> classes) {
        if (originAirport == destinationAirport) {
            throw new IllegalArgumentException("Source and destination Airport are the same");
        }

        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.airline = airline;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.classes = classes;
    }

    public String getAirlineCode() {
        return this.airline != null ? this.airline.getCode() : null;
    }

    public int getAvailableSeatCount() {
        return classes.stream()
                .mapToInt(FlightClass::getAvailableSeats)
                .sum();
    }

    public boolean checkAvailability(List<BookingPassenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return true; // No passengers means no need to check availability
        }

        // Check by class
        for (FlightClass flightClass : classes) {
            int availableSeats = flightClass.getAvailableSeats();
            int requiredSeats = (int) passengers.stream()
                    .filter(p -> p.getFlightClass().equals(flightClass.getFlightClass()))
                    .count();
            if (availableSeats < requiredSeats) {
                return false;
            }
        }
        return true;
    }

    public void decreaseAvailableSeats(List<FlightClassSeatCountDTO> flightClassSeatCounts) {
        for (FlightClassSeatCountDTO flightClassSeatCount : flightClassSeatCounts) {
            FlightClass flightClass = classes.stream()
                    .filter(c -> c.getFlightClass().equals(flightClassSeatCount.getFlightClass()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Flight Class"));
            flightClass.setAvailableSeats(flightClass.getAvailableSeats() - flightClassSeatCount.getSeatCount());
        }
    }
}
