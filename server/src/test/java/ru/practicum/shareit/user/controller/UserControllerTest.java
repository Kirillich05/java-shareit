package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = new User(1, "Nick", "nick@ya.ru");
        userDto = new UserDto(1, "Nick", "nick@ya.ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        when(service.getAllUsers())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserById() {
        long userId = 1;
        when(service.getUserById(userId))
                .thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(service, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void addUser() {
        when(service.addUser(user))
                .thenReturn(user);

        String actual = mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), actual);
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        long userId = 1;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeUserById(userId);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        long userId = 1;

        when(service.updateUser(userId, userDto))
                .thenReturn(user);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).updateUser(userId, userDto);
    }
}
