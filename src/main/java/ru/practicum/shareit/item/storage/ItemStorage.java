package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Long itemId, Item updatedItem);

    Optional<Item> findItemById(Long itemId);

    Collection<Item> getAllItems();

    Collection<Item> getItemsByOwnerId(Long ownerId);

    void deleteItemById(Long itemId);

    void deleteAllItems();

    List<Item> searchItems(String text);
}