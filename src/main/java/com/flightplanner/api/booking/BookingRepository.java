package com.flightplanner.api.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT coalesce(SUM(b.numberOfSeats), 0) FROM Booking b WHERE b.flight.id = :flightId")
    long countBookedSeatsForFlight(Long flightId);

    @Query("SELECT b FROM Booking b WHERE b.user.username = :username")
    List<Booking> findAllByUsername(String username);
}
