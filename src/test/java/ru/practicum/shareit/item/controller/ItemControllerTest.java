package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemBookingDto itemBookingDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;
    private User user;
    private Item item;

    @BeforeEach
    public void setup() {
        itemBookingDto = new ItemBookingDto(1, "name", "description",
                true, null, null, null);
        itemDto = new ItemDto(1, "name", "description", true, null);

        user = new User();
        user.setId(1);
        user.setName("John");
        user.setEmail("john@yandex.ru");
        item = new Item(1, "name", "description", true, user, null);

        commentDto = new CommentDto(1, "comment", user.getName(), null);
        comment = new Comment(1, "comment", item, user, null);
    }

    @SneakyThrows
    @Test
    void getAllItems() {
        int from = 0;
        int size = 0;
        long userId = 1;

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType("application/json")
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        long itemId = 1;
        long userId = 1;

        when(service.getItemById(anyLong(), anyLong()))
                .thenReturn(itemBookingDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(service, times(1)).getItemById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void addItem() {
        long userId = 1;
        when(service.addItem(itemDto, userId))
                .thenReturn(ItemMapper.toItem(itemDto, user));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(service, times(1)).addItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        long userId = 1;
        long itemId = 1;
        itemDto.setName("newName");

        when(service.updateItem(itemId, itemDto, userId))
                .thenReturn(item);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).updateItem(itemId, itemDto, userId);
    }

    @SneakyThrows
    @Test
    void search() {
        int from = 0;
        int size = 10;
        String text = "searchedText";

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).search(text, from, size);
    }

    @SneakyThrows
    @Test
    void search_whenEmptyText() {
        int from = 0;
        int size = 10;
        String text = "";

        String actual = mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of()), actual);
    }

    @SneakyThrows
    @Test
    void addComment() {
        long userId = 1;
        long itemId = 1;

        when(service.addComment(userId, itemId, commentDto))
                .thenReturn(comment);

        String actual = mockMvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), actual);
    }
}
