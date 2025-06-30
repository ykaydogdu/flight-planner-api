package com.flightplanner.api.booking;

import com.flightplanner.api.booking.passenger.BookingPassenger;
import com.flightplanner.api.user.User;
import com.flightplanner.api.flight.Flight;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingPassenger> passengers;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    public Booking(Flight flight, User user, List<BookingPassenger> passengers) {
        this.flight = flight;
        this.user = user;
        this.passengers = passengers;
        this.totalPrice = passengers.stream()
                .mapToDouble(BookingPassenger::getPriceAtBooking)
                .sum();
    }
}
