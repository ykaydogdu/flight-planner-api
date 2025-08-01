package com.flightplanner.api.booking;

import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    @Operation(summary = "Book a flight", description = "Books a flight with the specified ID using the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight booked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking request data"),
            @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    public ResponseEntity<BookingResponseDTO> bookFlight(@RequestBody BookingRequestDTO dto) {
        BookingResponseDTO bookingResponseDTO = bookingService.bookFlight(dto);
        return new ResponseEntity<>(bookingResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieves a booking with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        BookingResponseDTO bookingResponseDTO = bookingService.getBookingById(id);
        return new ResponseEntity<>(bookingResponseDTO, HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(summary = "Get booking by flight ID", description = "Retrieves a booking with the specified flight ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retreived successfully"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<List<BookingResponseDTO>> getBookingByFlightId(@RequestParam Long flightId) {
        List<BookingResponseDTO> bookingResponseDTOs = bookingService.getBookingByFlightId(flightId);
        return new ResponseEntity<>(bookingResponseDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a booking by ID", description = "Deletes a booking with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<Void> deleteBooking(@PathVariable(name = "id") Long id) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get all bookings for the authenticated user", description = "Retrieves all bookings made by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings() {
        List<BookingResponseDTO> bookings = bookingService.getMyBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
