package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingShortDto;

public class BookingShortMapper {

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingShortDto dto) {
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .build();
    }
}