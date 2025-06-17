package ru.practicum.shareit.user;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("in-memory")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final Set<String> emails = new HashSet<>();

    @Override
    public User addUser(User user) {
        if (!emails.add(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User updateUser) {
        User existingUser = users.get(id);
        if (existingUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        if (!Objects.equals(updateUser.getEmail(), existingUser.getEmail())) {
            if (!emails.add(updateUser.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            emails.remove(existingUser.getEmail());
        }
        updateUser.setId(id);
        users.put(id, updateUser);
        return updateUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }
}