package com.flightplanner.api.flight;

import com.flightplanner.api.flight.dto.FlightStatisticsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flightplanner.api.flight.dto.FlightDTO;

import java.time.LocalDate;
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
        select new com.flightplanner.api.flight.dto.FlightDTO(
            f.id,
            (select min(fc.price) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.availableSeats) from FlightClass fc where fc.flight.id = f.id),
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
        and (:includePast = true or f.departureTime >= current_timestamp)
        and (
            :passengerEconomy = 0 or (select fc.availableSeats from FlightClass fc where fc.flight.id = f.id and fc.flightClass = 'ECONOMY') >= :passengerEconomy
        )
        and (
            :passengerBusiness = 0 or (select fc.availableSeats from FlightClass fc where fc.flight.id = f.id and fc.flightClass = 'BUSINESS') >= :passengerBusiness
        )
        and (
            :passengerFirstClass = 0 or (select fc.availableSeats from FlightClass fc where fc.flight.id = f.id and fc.flightClass = 'FIRST_CLASS') >= :passengerFirstClass
        )
        group by f.id
    """)
    List<FlightDTO> findFilteredFlights(
            @Param("airlineCode") String airlineCode,
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("departureDateStart") LocalDateTime departureDateStart,
            @Param("departureDateEnd") LocalDateTime departureDateEnd,
            @Param("includePast") Boolean includePast,
            @Param("passengerEconomy") Integer passengerEconomy,
            @Param("passengerBusiness") Integer passengerBusiness,
            @Param("passengerFirstClass") Integer passengerFirstClass
    );

    @Query("""
        select new com.flightplanner.api.flight.dto.FlightDTO(
            f.id,
            (select min(fc.price) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id),
            (select sum(fc.availableSeats) from FlightClass fc where fc.flight.id = f.id),
            f.departureTime,
            f.duration,
            f.arrivalTime,
            f.airline,
            f.originAirport,
            f.destinationAirport
        ) from Flight f
        where f.id = :id
        and (select sum(fc.seatCount) from FlightClass fc where fc.flight.id = f.id) > 0
        group by f.id
    """)
    Optional<FlightDTO> findByIdWithEmptySeats(@Param("id") Long id);

    @Query("""
    SELECT new com.flightplanner.api.flight.dto.FlightStatisticsDTO(
        f.id,
        COUNT(DISTINCT b.id),
        SUM(fc.seatCount - fc.availableSeats),
        SUM((fc.seatCount - fc.availableSeats) * fc.price)
    )
    FROM Flight f
    LEFT JOIN Booking b ON b.flight.id = f.id
    LEFT JOIN FlightClass fc ON fc.flight.id = f.id
    WHERE f.airline.code = :airlineCode
    AND f.departureTime >= current_timestamp
    AND (:startDate IS NULL OR f.departureTime >= :startDate)
    AND (:endDate IS NULL OR f.departureTime <= :endDate)
    GROUP BY f.id
""")
    List<FlightStatisticsDTO> getFlightStatistics(
            @Param("airlineCode") String airlineCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
