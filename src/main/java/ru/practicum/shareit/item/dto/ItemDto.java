package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

@Data
@Builder(toBuilder = true)
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = {OnCreate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {OnCreate.class})
    private String description;

    @NotNull(message = "Поле available не может быть null", groups = {OnCreate.class})
    private Boolean available;
}