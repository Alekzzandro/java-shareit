package ru.practicum.shareit.booking;

import java.util.Collection;
import java.util.Optional;

public interface BookingStorage {

    Booking addBooking(Booking booking);

    Booking updateBooking(Long bookingId, Booking updatedBooking);

    Optional<Booking> findBookingById(Long bookingId);

    Collection<Booking> getAllBookings();

    Collection<Booking> getBookingsByBookerId(Long bookerId);

    Collection<Booking> getBookingsByItemId(Long itemId);

    void deleteBookingById(Long bookingId);

    void deleteAllBookings();
}