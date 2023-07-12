package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserServiceImplTest {

    @Autowired
    private UserRepository repo;
    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserServiceImpl(repo);
    }

    @Test
    void getAllUsers() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        service.addUser(user);

        List<User> userList = service.getAllUsers();
        User savedUser = userList.get(0);

        assertThat(savedUser).isNotNull();
    }

    @Test
    void getUserById_whenNotFoundId() {
        long unrealUserId = 500;

        assertThrows(NotFoundException.class, () -> service.getUserById(unrealUserId));
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        service.addUser(user);

        assertThat(user).isEqualTo(service.getUserById(user.getId()));
    }

    @Test
    void addUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        service.addUser(user);

        List<User> userList = service.getAllUsers();
        User savedUser = userList.get(0);

        assertThat(user).isEqualTo(savedUser);
    }

    @Test
    void addUser_whenEmailIsNull() {
        User user = new User();
        user.setName("John");
        user.setEmail(null);

        assertThrows(BadRequestException.class, () -> {
            User savedUser = service.addUser(user);

            User foundUser = service.getUserById(savedUser.getId());

            assertThat(foundUser).isNull();
        });
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        User savedUser = service.addUser(user);

        savedUser.setEmail("master@google.com");
        service.updateUser(savedUser.getId(), UserMapper.toUserDto(savedUser));
        User foundUser = service.getUserById(savedUser.getId());

        assertThat(foundUser.getName()).isEqualTo("John");
        assertThat(foundUser.getEmail()).isEqualTo("master@google.com");
    }

    @Test
    void updateUser_whenUserNotFound() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");
        User savedUser = service.addUser(user);

        long unrealUserId = 500;

        assertThrows(NotFoundException.class, () -> service.updateUser(unrealUserId, UserMapper.toUserDto(savedUser)));
    }

    @Test
    void updateUser_whenUserEmailForUpgradeIsExisted() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        User savedUser = service.addUser(user);

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setEmail(user.getEmail());

        assertEquals(user, service.updateUser(savedUser.getId(), userDtoForUpdate));
    }

    @Test
    void updateUser_whenUserEmailForUpgradeIsNull() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        User savedUser = service.addUser(user);

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setName("Nick");

        User updatedUser = service.updateUser(savedUser.getId(), userDtoForUpdate);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals("Nick", updatedUser.getName());
    }

    @Test
    void updateUser_whenUserNameForUpgradeIsNull() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        User savedUser = service.addUser(user);

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setEmail("master@google.com");

        User updatedUser = service.updateUser(savedUser.getId(), userDtoForUpdate);
        assertEquals("John", updatedUser.getName());
        assertEquals("master@google.com", updatedUser.getEmail());
    }

    @Test
    void removeUserById() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        assertThrows(NotFoundException.class, () -> {
            User savedUser = service.addUser(user);

            service.removeUserById(savedUser.getId());
            User foundUser = service.getUserById(savedUser.getId());

            assertThat(foundUser).isNull();
        });
    }
}