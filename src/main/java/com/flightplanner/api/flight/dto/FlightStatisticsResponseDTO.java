package com.flightplanner.api.flight.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightStatisticsResponseDTO {
    private long overallBookingCount;
    private long overallPassengerCount;
    private double overallRevenue;
    private List<FlightStatisticsDTO> flightStats;
}
