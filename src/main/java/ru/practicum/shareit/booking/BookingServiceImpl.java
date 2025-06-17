package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingCreateDto createDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        Item item = itemRepository.findById(createDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + createDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь с ID=" + createDto.getItemId() + " недоступна для бронирования");
        }

        boolean isOverlapping = bookingRepository.existsByItemIdAndDateOverlap(
                createDto.getItemId(),
                createDto.getStart(),
                createDto.getEnd()
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("Бронирование пересекается с существующими бронированиями");
        }

        Booking booking = BookingMapper.toBooking(createDto, item, booker);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование с ID=" + bookingId + " не найдено"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        return BookingMapper.toBookingDto(
                bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено"))
        );
    }

    @Override
    public List<BookingShortDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId) {
        if (!Objects.equals(userId, requesterId)) {
            throw new ForbiddenException("Пользователь с ID=" + requesterId + " не имеет прав доступа");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }

        var bookings = bookingRepository.findByBookerId(userId);

        var filteredBookings = filterBookingsByState(bookings, state);

        return filteredBookings.stream()
                .map(BookingMapper::toBookingShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingShortDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID=" + ownerId + " не найден");
        }

        var bookings = bookingRepository.findByItem_Owner_Id(ownerId);

        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::toBookingShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookings(BookingState state, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Пользователь с ID=" + requesterId + " не найден");
        }

        List<Booking> bookings = bookingRepository.findByBookerId(requesterId);

        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, BookingState state) {
        return switch (state) {
            case ALL -> bookings;
            case CURRENT -> bookings.stream()
                    .filter(b -> b.getStartDate()
                            .isBefore(LocalDateTime.now()) && b.getEndDate()
                            .isAfter(LocalDateTime.now()))
                    .toList();
            case PAST -> bookings.stream()
                    .filter(b -> b.getEndDate().isBefore(LocalDateTime.now()))
                    .toList();
            case FUTURE -> bookings.stream()
                    .filter(b -> b.getStartDate().isAfter(LocalDateTime.now()))
                    .toList();
            case WAITING -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .toList();
            case REJECTED -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .toList();
        };
    }
}