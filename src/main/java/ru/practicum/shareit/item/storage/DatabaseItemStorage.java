package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Profile("!in-memory")
public class DatabaseItemStorage implements ItemStorage {
    private final ItemRepository itemRepository;

    public DatabaseItemStorage(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, Item updatedItem) {
        if (!itemRepository.existsById(itemId)) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }
        updatedItem.setId(itemId);
        return itemRepository.save(updatedItem);
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public Collection<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Collection<Item> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public void deleteItemById(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public void deleteAllItems() {
        itemRepository.deleteAll();
    }
}