package com.flightplanner.api.airline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AirlineWithStaffCountDTO {
    private String code;
    private String name;
    private long staffCount;
}
