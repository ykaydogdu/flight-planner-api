package com.flightplanner.api.flight.classes;

import com.flightplanner.api.flight.Flight;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightClassId implements Serializable {
    private Flight flight;
    private FlightClassEnum flightClass;
}
