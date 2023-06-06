package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(long id);

    User addUser(User user);

    User updateUser(long id, UserDto userDto);

    void removeUserById(long id);
}
