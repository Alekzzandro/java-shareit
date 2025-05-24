package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User toUser(UserDto dto) {
        return User.builder()
                .userId(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}