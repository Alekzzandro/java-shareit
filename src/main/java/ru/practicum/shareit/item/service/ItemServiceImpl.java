package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        User owner = findUserById(userId);
        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        User owner = findUserById(userId);
        Item existingItem = findItemById(itemId);

        if (!Objects.equals(existingItem.getOwner().getUserId(), userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        ItemDto existingItemDto = ItemMapper.toItemDto(existingItem);

        ItemDto updatedItemDto = existingItemDto.toBuilder()
                .name(dto.getName() != null ? dto.getName() : existingItemDto.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : existingItemDto.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : existingItemDto.getAvailable())
                .build();

        Item updatedItem = ItemMapper.toItem(updatedItemDto);
        updatedItem.setOwner(existingItem.getOwner());

        Item savedItem = itemStorage.updateItem(itemId, updatedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(findItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId, int from, int size) {
        List<Item> allItems = itemStorage.getAllItems().stream()
                .collect(Collectors.toList());

        int limit = 2;
        int startIndex = 0;
        int endIndex = Math.min(startIndex + limit, allItems.size());

        return allItems.subList(startIndex, endIndex).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(Long itemId) {
        Item item = findItemById(itemId);
        User owner = findUserById(item.getOwner().getUserId());

        if (!Objects.equals(owner.getUserId(), item.getOwner().getUserId())) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        itemStorage.deleteItemById(itemId);
    }

    @Override
    public void deleteAllItems() {
        itemStorage.deleteAllItems();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();

        List<Item> items = itemStorage.searchItems(lowerCaseText);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private User findUserById(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена"));
    }
}