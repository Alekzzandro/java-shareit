package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid BookingCreateDto createDto) {
        return bookingService.addBooking(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingService.getAllBookings(state, requesterId);
    }

    @GetMapping("/users/{userId}/bookings")
    public List<BookingShortDto> getAllBookingsByUser(
            @PathVariable Long userId,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByUser(userId, state, requesterId);
    }

    @GetMapping("/bookings/owner")
    public List<BookingShortDto> getAllBookingsForOwnerItems(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.getAllBookingsForOwnerItems(ownerId, state);
    }
}