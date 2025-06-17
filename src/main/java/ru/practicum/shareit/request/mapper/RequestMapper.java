package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.Request;

import java.time.LocalDateTime;

public class RequestMapper {

    public static Request toRequest(RequestDto dto) {
        return Request.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .created(dto.getCreated() != null ? dto.getCreated() : LocalDateTime.now())
                .build();
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor().getId())
                .created(request.getCreated())
                .build();
    }
}