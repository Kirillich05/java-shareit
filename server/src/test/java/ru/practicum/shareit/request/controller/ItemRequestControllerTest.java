package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDtoRequire itemRequestDtoRequire;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private ItemRequest itemRequest;
    private User requestor;

    @BeforeEach
    void setup() {
        requestor = new User(1, "Nick", "nick@ya.ru");
        itemRequestDtoRequire = new ItemRequestDtoRequire(1, "description");
        itemRequestDto = new ItemRequestDto(1, "description", requestor, null);
        itemRequestDtoResponse = new ItemRequestDtoResponse(1, "description", requestor.getId(), null, null);
        itemRequest = new ItemRequest(1, "description", requestor, null);
    }

    @SneakyThrows
    @Test
    void getAllItemRequest() {
        when(service.getAllItemRequestByUser(requestor.getId()))
                .thenReturn(List.of(itemRequestDtoResponse));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllItemRequestByUser(requestor.getId());
    }

    @SneakyThrows
    @Test
    void addItemRequest() {
        when(service.addItemRequest(requestor.getId(), itemRequestDtoRequire))
                .thenReturn(itemRequest);

        String actual = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .content(new ObjectMapper().writeValueAsString(itemRequestDtoRequire))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), actual);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(service.getItemRequestById(requestor.getId(), 1))
                .thenReturn(itemRequestDtoResponse);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(service, times(1)).getItemRequestById(requestor.getId(), 1);
    }

    @SneakyThrows
    @Test
    void getAllItemRequest_withPages() {
        int from = 0;
        int size = 10;

        when(service.getAllItemRequest(requestor.getId(), from, size))
                .thenReturn(List.of(itemRequestDtoResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllItemRequest(requestor.getId(), from, size);
    }
}
