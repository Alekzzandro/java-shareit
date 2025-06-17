package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, RequestDto requestDto);

    RequestDto getRequestById(Long requestId);

    List<RequestDto> getAllRequestsByUser(Long userId);

    List<RequestDto> getAllRequestsExcludingUser(Long userId);
}