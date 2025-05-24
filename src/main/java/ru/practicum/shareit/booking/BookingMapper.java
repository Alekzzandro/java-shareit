package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .bookerId(booking.getBooker().getUserId())
                .status(booking.getStatus())
                .build();
    }
}