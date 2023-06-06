package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long lastId = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            user.setId(getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getId() {
        lastId++;
        return lastId;
    }
}
