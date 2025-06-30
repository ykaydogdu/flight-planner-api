package com.flightplanner.api.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flightplanner.api.flight.dto.FlightResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        select new com.flightplanner.api.flight.dto.FlightResponseDTO(
            f.id,
            (select min(fc.price) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id) - coalesce((select count(bp.id) from Booking b join b.passengers bp where b.flight.id = f.id), 0),
            f.departureTime,
            f.duration,
            f.arrivalTime,
            f.airline,
            f.originAirport,
            f.destinationAirport
        ) from Flight f
        where (:airlineCode is null or f.airline.code = :airlineCode)
        and (:originAirportCode is null or f.originAirport.code = :originAirportCode)
        and (:destinationAirportCode is null or f.destinationAirport.code = :destinationAirportCode)
        and (:departureDateStart is null or :departureDateEnd is null or f.departureTime between :departureDateStart and :departureDateEnd)
        group by f.id
    """)
    List<FlightResponseDTO> findFilteredFlights(
            @Param("airlineCode") String airlineCode,
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("departureDateStart") LocalDateTime departureDateStart,
            @Param("departureDateEnd") LocalDateTime departureDateEnd
    );

    @Query("""
        select new com.flightplanner.api.flight.dto.FlightResponseDTO(
            f.id,
            (select min(fc.price) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id) - coalesce((select count(bp.id) from Booking b join b.passengers bp where b.flight.id = f.id), 0),
            f.departureTime,
            f.duration,
            f.arrivalTime,
            f.airline,
            f.originAirport,
            f.destinationAirport
        ) from Flight f
        group by f.id
    """)
    List<FlightResponseDTO> findAllWithEmptySeats();

    @Query("""
        select new com.flightplanner.api.flight.dto.FlightResponseDTO(
            f.id,
            (select min(fc.price) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id) - coalesce((select count(bp.id) from Booking b join b.passengers bp where b.flight.id = f.id and b.flight.id = :id), 0),
            f.departureTime,
            f.duration,
            f.arrivalTime,
            f.airline,
            f.originAirport,
            f.destinationAirport
        ) from Flight f
        where f.id = :id
        group by f.id
    """)
    Optional<FlightResponseDTO> findByIdWithEmptySeats(@Param("id") Long id);
}
