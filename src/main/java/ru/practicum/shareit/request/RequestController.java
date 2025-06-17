package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody RequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }

    @GetMapping
    public List<RequestDto> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequestsExcludingUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequestsExcludingUser(userId);
    }
}