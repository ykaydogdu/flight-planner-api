package com.flightplanner.api.flight.dto;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.airport.AirportRepository;
import com.flightplanner.api.airport.AirportService;
import com.flightplanner.api.flight.Flight;
import com.flightplanner.api.flight.classes.FlightClass;
import com.flightplanner.api.timezone.TimezoneService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

@Service
public class FlightMapper {

    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final AirportService airportService;
    private final TimezoneService timeZoneService;

    public FlightMapper(AirlineRepository airlineRepository,
                        AirportRepository airportRepository,
                        AirportService airportService,
                        TimezoneService timeZoneService) {
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.airportService = airportService;
        this.timeZoneService = timeZoneService;
    }

    /**
     * Converts a FlightRequestDTO to a Flight entity.
     */
    public Flight toEntity(final FlightRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Airline airline = airlineRepository.findById(dto.getAirlineCode())
                .orElseThrow(() -> new NotFoundException("Airline", new HashMap<>(){{
                    put("code", dto.getAirlineCode());
                }}));
        Airport originAirport = airportRepository.findById(dto.getOriginAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getOriginAirportCode());
                }}));
        Airport destinationAirport = airportRepository.findById(dto.getDestinationAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getDestinationAirportCode());
                }}));

        // Calculate departure time
        LocalDateTime departureTime = dto.getDepartureTime();
        TimeZone originTimezone = timeZoneService.getTimezone(originAirport.getLatitude(), originAirport.getLongitude());
        departureTime = timeZoneService.convertLocalDateTimeToUtc(departureTime, originTimezone);

        // Calculate arrival time
        LocalDateTime arrivalTime = departureTime.plusMinutes(dto.getDuration());
        TimeZone destinationTimezone = timeZoneService.getTimezone(destinationAirport.getLatitude(), destinationAirport.getLongitude());
        arrivalTime = timeZoneService.convertLocalDateTimeToUtc(arrivalTime, destinationTimezone);

        return new Flight(
                departureTime,
                dto.getDuration(),
                arrivalTime,
                airline,
                originAirport,
                destinationAirport,
                dto.getFlightClasses()
        );
    }

    public void fixTimeZone(FlightResponseDTO dto) {
        TimeZone originTimezone = timeZoneService.getTimezone(dto.getOriginAirport().getLatitude(), dto.getOriginAirport().getLongitude());
        dto.setDepartureTime(timeZoneService.convertUtcToLocalDateTime(dto.getDepartureTime(), originTimezone));
        TimeZone destinationTimezone = timeZoneService.getTimezone(dto.getDestinationAirport().getLatitude(), dto.getDestinationAirport().getLongitude());
        dto.setArrivalTime(timeZoneService.convertUtcToLocalDateTime(dto.getArrivalTime(), destinationTimezone));
    }

    public Flight updateEntity(Flight flight, FlightRequestDTO requestDTO) {
        flight.setOriginAirport(airportService.getAirportByCode(requestDTO.getOriginAirportCode()));
        flight.setDestinationAirport(airportService.getAirportByCode(requestDTO.getDestinationAirportCode()));
        flight.setDepartureTime(requestDTO.getDepartureTime());
        flight.setDuration(requestDTO.getDuration());
        flight.setClasses(requestDTO.getFlightClasses());
        return flight;
    }

    public FlightResponseClassDTO toResponseClassDTO(FlightResponseDTO flightResponseDTO, List<FlightClassDTO> flightClasses) {
        return FlightResponseClassDTO.builder()
                .id(flightResponseDTO.getId())
                .minPrice(flightResponseDTO.getMinPrice())
                .seatCount(flightResponseDTO.getSeatCount())
                .emptySeats(flightResponseDTO.getEmptySeats())
                .departureTime(flightResponseDTO.getDepartureTime())
                .duration(flightResponseDTO.getDuration())
                .arrivalTime(flightResponseDTO.getArrivalTime())
                .airline(flightResponseDTO.getAirline())
                .originAirport(flightResponseDTO.getOriginAirport())
                .destinationAirport(flightResponseDTO.getDestinationAirport())
                .classes(flightClasses)
                .build();
    }

    public FlightClassDTO toClassDTO(FlightClass flightClass) {
        return FlightClassDTO.builder()
                .flightClass(flightClass.getFlightClass())
                .seatCount(flightClass.getSeatCount())
                .availableSeats(flightClass.getAvailableSeats())
                .price(flightClass.getPrice())
                .build();
    }
}


