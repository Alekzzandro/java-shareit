package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class ItemRequest {
    private Long requestId;
    private String description;
    private User requester;
    private LocalDateTime created;
}