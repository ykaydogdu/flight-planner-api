package com.flightplanner.api.booking.passenger;

import com.flightplanner.api.booking.Booking;
import com.flightplanner.api.flight.classes.FlightClassEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking_passengers")
public class BookingPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private FlightClassEnum flightClass;

    private double priceAtBooking;
}
