package com.flightplanner.api.flight.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightStatisticsDTO {
    private Long flightId;
    private Long bookingCount;
    private Long passengerCount;
    private Double revenue;
}
