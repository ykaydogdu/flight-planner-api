package com.flightplanner.api.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
        select count(f) from Flight f where f.airline.code = :airlineCode
        and f.srcAirport.code = :srcAirportCode
        and f.destAirport.code = :destAirportCode
        and f.departureTime between :startOfDay and :endOfDay
    """)
    long dailyFlightCount(
            @Param("airlineCode") String airlineCode,
            @Param("srcAirportCode") String srcAirportCode,
            @Param("destAirportCode") String destAirportCode,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
