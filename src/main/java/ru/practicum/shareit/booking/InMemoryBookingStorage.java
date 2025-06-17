package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Profile("in-memory")
public class InMemoryBookingStorage implements BookingStorage {

    private final Map<Long, Booking> bookings = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Booking addBooking(Booking booking) {
        booking.setId(nextId.getAndIncrement());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        if (!bookings.containsKey(bookingId)) {
            throw new NoSuchElementException("Бронирование с ID=" + bookingId + " не найдено");
        }
        updatedBooking.setId(bookingId);
        bookings.put(bookingId, updatedBooking);
        return updatedBooking;
    }

    @Override
    public Optional<Booking> findBookingById(Long bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public Collection<Booking> getAllBookings() {
        return Collections.unmodifiableCollection(bookings.values());
    }

    @Override
    public Collection<Booking> getBookingsByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Booking> getBookingsByItemId(Long itemId) {
        return bookings.values().stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBookingById(Long bookingId) {
        if (!bookings.containsKey(bookingId)) {
            throw new NoSuchElementException("Бронирование с ID=" + bookingId + " не найдено");
        }
        bookings.remove(bookingId);
    }

    @Override
    public void deleteAllBookings() {
        bookings.clear();
    }
}