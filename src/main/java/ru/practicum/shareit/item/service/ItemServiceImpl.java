package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        User owner = userStorage.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        Item existingItem = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        if (dto.getName() != null) existingItem.setName(dto.getName());
        if (dto.getDescription() != null) existingItem.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existingItem.setAvailable(dto.getAvailable());

        Item updatedItem = itemStorage.updateItem(itemId, existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        return ItemMapper.toItemDtoWithComments(item, comments);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        Collection<Item> items = itemStorage.getItemsByOwnerId(userId);
        return items.stream()
                .map(this::mapItemWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        Collection<Item> items = itemStorage.getItemsByOwnerId(ownerId);
        return items.stream()
                .map(this::mapItemWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        return itemStorage.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemStorage.deleteItemById(itemId);
    }

    @Override
    public void deleteAllItems() {
        itemStorage.deleteAllItems();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        Item item = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена"));

        boolean hasBooking = bookingRepository.existsByUserAndItemAndApprovedStatus(userId, itemId);
        if (!hasBooking) {
            throw new BadRequestException("Пользователь с ID=" + userId + " не имеет права оставлять комментарий");
        }

        boolean isBookingCompleted = bookingRepository.existsByUserAndItemAndApprovedStatusAndEndDateBefore(
                userId, itemId, LocalDateTime.now());
        if (!isBookingCompleted) {
            throw new BadRequestException("Бронирование для пользователя с ID=" + userId + " еще не завершено");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    private ItemDto mapItemWithBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), now).orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), now).orElse(null);
        return ItemMapper.toItemDtoWithBookings(item, lastBooking, nextBooking);
    }

}