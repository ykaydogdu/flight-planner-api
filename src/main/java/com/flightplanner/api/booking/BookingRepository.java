package com.flightplanner.api.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Finds all bookings made by a user identified by their username.
     *
     * @param username the username of the user whose bookings are to be retrieved
     * @return a list of bookings associated with the specified username
     */
    @Query("SELECT b FROM Booking b WHERE b.user.username = :username")
    List<Booking> findAllByUsername(String username);
}
