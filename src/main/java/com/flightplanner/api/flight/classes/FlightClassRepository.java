package com.flightplanner.api.flight.classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightClassRepository extends JpaRepository<FlightClass, FlightClassId> {
    @Query("""
        select fc from FlightClass fc where fc.flight.id in :flightIds
    """)
    List<FlightClass> findByFlightIds(@Param("flightIds") List<Long> flightIds);
}
