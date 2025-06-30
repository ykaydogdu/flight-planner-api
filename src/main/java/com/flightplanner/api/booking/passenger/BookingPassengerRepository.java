package com.flightplanner.api.booking.passenger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    /**
     * Finds all passengers associated with a booking identified by its ID.
     *
     * @param bookingId the ID of the booking whose passengers are to be retrieved
     * @return a list of BookingPassenger entities associated with the specified booking ID
     */
    @Query("SELECT bp FROM BookingPassenger bp WHERE bp.booking.id = :bookingId")
    List<BookingPassenger> findAllByBookingId(Long bookingId);
}
