package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private final UserRepository repo;
    private  UserService service;

    @BeforeEach
    void setup() {
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
    void getUserById() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        service.addUser(user);

        assertThat(user).isEqualTo(service.getUserById(user.getId()));
    }

    @Test
    void getUserById_whenNotFoundId() {
        long unrealUserId = 500;

        assertThrows(NotFoundException.class, () -> service.getUserById(unrealUserId));
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
    void updateUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@yandex.ru");

        User savedUser = service.addUser(user);

        savedUser.setEmail("master@google.com");
        service.updateUser(savedUser.getId(), UserMapper.toUserDto(savedUser));
        User foundUser = service.getUserById(savedUser.getId());

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
