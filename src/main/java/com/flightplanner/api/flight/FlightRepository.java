package com.flightplanner.api.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
        select count(f) from Flight f where f.airline.code = :airlineCode
        and f.originAirport.code = :originAirportCode
        and f.destinationAirport.code = :destinationAirportCode
        and f.departureTime between :startOfDay and :endOfDay
    """)
    long dailyFlightCount(
            @Param("airlineCode") String airlineCode,
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
        select f from Flight f
        where (:airlineCode is null or f.airline.code = :airlineCode)
        and (:originAirportCode is null or f.originAirport.code = :originAirportCode)
        and (:destinationAirportCode is null or f.destinationAirport.code = :destinationAirportCode)
        and (:departureDateStart is null or :departureDateEnd is null or f.departureTime between :departureDateStart and :departureDateEnd)
    """)
    List<Flight> findFilteredFlights(
            @Param("airlineCode") String airlineCode,
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("departureDateStart") LocalDateTime departureDateStart,
            @Param("departureDateEnd") LocalDateTime departureDateEnd
    );
}
