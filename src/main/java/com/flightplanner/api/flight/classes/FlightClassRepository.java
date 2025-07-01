package com.flightplanner.api.flight.classes;

import com.flightplanner.api.flight.dto.FlightClassDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightClassRepository extends JpaRepository<FlightClass, FlightClassId> {
    @Query("""
        select new com.flightplanner.api.flight.dto.FlightClassDTO(fc.flightClass, fc.seatCount, fc.availableSeats, fc.price, fc.flight.id) from FlightClass fc where fc.flight.id in :flightIds
    """)
    List<FlightClassDTO> findByFlightIds(@Param("flightIds") List<Long> flightIds);

    @Query("""
        select new com.flightplanner.api.flight.dto.FlightClassDTO(fc.flightClass, fc.seatCount, fc.availableSeats, fc.price, fc.flight.id) from FlightClass fc where fc.flight.id = :flightId
    """)
    List<FlightClassDTO> findByFlightId(@Param("flightId") Long flightId);
}
