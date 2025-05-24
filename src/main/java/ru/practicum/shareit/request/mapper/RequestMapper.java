package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;

public class RequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .requestId(itemRequest.getRequestId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getUserId())
                .created(itemRequest.getCreated())
                .build();
    }
}