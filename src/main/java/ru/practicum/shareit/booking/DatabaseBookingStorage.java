package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@Profile("!in-memory")
public class DatabaseBookingStorage implements BookingStorage {
    private final BookingRepository bookingRepository;

    public DatabaseBookingStorage(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking addBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        updatedBooking.setId(bookingId);
        return bookingRepository.save(updatedBooking);
    }

    @Override
    public Optional<Booking> findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public Collection<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Collection<Booking> getBookingsByBookerId(Long bookerId) {
        return bookingRepository.findByBookerId(bookerId);
    }

    @Override
    public Collection<Booking> getBookingsByItemId(Long itemId) {
        return bookingRepository.findByItemId(itemId);
    }

    @Override
    public void deleteBookingById(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public void deleteAllBookings() {
        bookingRepository.deleteAll();
    }
}