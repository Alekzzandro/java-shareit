package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingCreateDto createDto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId);

    List<BookingShortDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId);

    List<BookingShortDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state);

    List<BookingDto> getAllBookings(BookingState state, Long requesterId);
}