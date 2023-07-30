package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(long id) {
        return findOrThrow(id);
    }

    @Transactional
    @Override
    public User addUser(User user) {
        if (user.getEmail() == null ||
                user.getName() == null) {
            throw new BadRequestException("no name and email");
        }
        return repo.save(user);
    }

    @Transactional
    @Override
    public User updateUser(long id, UserDto userDto) {
        var user = findOrThrow(id);
        var userForUpgrade = UserMapper.toUser(userDto);

        if (Objects.equals(userForUpgrade.getEmail(), user.getEmail())) {
            return user;
        }

        if (userForUpgrade.getName() != null) user.setName(userForUpgrade.getName());
        if (userForUpgrade.getEmail() != null) user.setEmail(userForUpgrade.getEmail());

        return repo.save(user);
    }

    @Transactional
    @Override
    public void removeUserById(long id) {
        repo.deleteById(id);
    }

    private User findOrThrow(long id) {
        return repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User by id " + id + " was not found")
                );
    }
}
