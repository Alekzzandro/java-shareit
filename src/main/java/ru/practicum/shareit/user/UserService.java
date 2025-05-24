package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto dto);

    UserDto updateUser(Long id, UserDto dto);

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

    void deleteUserById(Long id);

    void deleteAllUsers();
}