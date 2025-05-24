package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        if (!userStorage.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }

        User owner = userStorage.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        Item existingItem = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

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
        return ItemMapper.toItemDto(
                itemStorage.findItemById(itemId)
                        .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"))
        );
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemStorage.getItemsByOwnerId(userId).stream()
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

        return itemStorage.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .map(ItemMapper::toItemDto)
                .toList();
    }
}