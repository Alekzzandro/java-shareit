package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDto createRequest(Long userId, RequestDto requestDto) {
        Request request = RequestMapper.toRequest(requestDto);
        request.setRequestor(User.builder().id(userId).build());
        request.setCreated(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);

        return RequestMapper.toRequestDto(savedRequest);
    }

    @Override
    public RequestDto getRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос с ID=" + requestId + " не найден"));

        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getAllRequestsByUser(Long userId) {
        return requestRepository.findByRequestorId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequestsExcludingUser(Long userId) {
        return requestRepository.findByRequestorIdNot(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}