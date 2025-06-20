package com.flightplanner.api.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT SUM(b.numberOfSeats) FROM Booking b WHERE b.flight.id = :flightId")
    int countBookedSeatsForFlight(Long flightId);
}
