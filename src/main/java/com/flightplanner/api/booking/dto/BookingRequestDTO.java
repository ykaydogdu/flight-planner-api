package com.flightplanner.api.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private long flightId;
    private String username;
    private List<BookingPassengerRequestDTO> passengers;

    public int getNumberOfSeats() {
        return passengers != null ? passengers.size() : 0;
    }
}
