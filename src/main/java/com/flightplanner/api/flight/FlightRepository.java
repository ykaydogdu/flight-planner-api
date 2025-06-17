package com.flightplanner.api.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
        select count(f) from Flight f where f.airlineCode = :airlineCode
        and f.srcAirportCode = :srcAirportCode
        and f.destAirportCode = :destAirportCode
        and f.departureTime between :startOfDay and :endOfDay
    """)
    long dailyFlightCount(
            @Param("airlineCode") String airlineCode,
            @Param("srcAirport") String srcAirportCode,
            @Param("destAirport") String destAirportCode,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
