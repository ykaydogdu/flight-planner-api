package com.flightplanner.api.flight.dto;

import com.flightplanner.api.flight.classes.FlightClassEnum;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightClassDTO {
    private FlightClassEnum flightClass;
    private int seatCount;
    private int availableSeats;
    private double price;
    private long flightId;
}
