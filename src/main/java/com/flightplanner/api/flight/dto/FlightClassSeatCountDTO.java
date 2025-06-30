package com.flightplanner.api.flight.dto;

import com.flightplanner.api.flight.classes.FlightClassEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlightClassSeatCountDTO {
    private FlightClassEnum flightClass;
    private int seatCount;
}
