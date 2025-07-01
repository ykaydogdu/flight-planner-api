package com.flightplanner.api.booking.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
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
