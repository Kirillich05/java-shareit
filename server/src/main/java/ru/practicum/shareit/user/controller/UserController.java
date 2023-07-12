package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Getting users list");
        return service.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") long id) {
        log.info("Getting user by id " + id);
        return UserMapper.toUserDto(service.getUserById(id));
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Posting user");
        var model = UserMapper.toUser(userDto);
        var user = service.addUser(model);

        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") long id) {
        log.info("Deleting user by id " + id);
        service.removeUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @PathVariable("id") long id,
            @RequestBody UserDto userDto
    ) {
        log.info("Updating user by id " + id);

        var updatedUser = service.updateUser(id, userDto);
        return UserMapper.toUserDto(updatedUser);
    }
}
