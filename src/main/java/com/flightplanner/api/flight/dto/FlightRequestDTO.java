package com.flightplanner.api.flight.dto;

import com.flightplanner.api.flight.classes.FlightClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequestDTO {
    private LocalDateTime departureTime;
    private int duration;
    private String airlineCode;
    private String originAirportCode;
    private String destinationAirportCode;
    private List<FlightClass> flightClasses;
}
