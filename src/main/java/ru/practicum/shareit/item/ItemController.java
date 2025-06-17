package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody @Validated(OnCreate.class) ItemDto dto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody @Validated(OnUpdate.class) ItemDto dto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/owner/{ownerId}")
    public Collection<ItemDto> getItemsByOwnerId(@PathVariable Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable Long itemId) {
        itemService.deleteItemById(itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllItems() {
        itemService.deleteAllItems();
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }
}