package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage userStorage;

    @Override
    public UserDto addUser(UserDto dto) {
        if (userStorage.getAllUsers().stream()
                .anyMatch(u -> Objects.equals(u.getEmail(), dto.getEmail()))) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        User user = UserMapper.toUser(dto);
        User savedUser = userStorage.addUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existingUser = userStorage.findUserById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"));

        User updatedUser = User.builder()
                .userId(id)
                .name(dto.getName() != null ? dto.getName() : existingUser.getName())
                .email(dto.getEmail() != null ? dto.getEmail() : existingUser.getEmail())
                .build();

        User savedUser = userStorage.updateUser(id, updatedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(
                userStorage.findUserById(id)
                        .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"))
        );
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}