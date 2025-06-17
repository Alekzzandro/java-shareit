package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User toUser(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}