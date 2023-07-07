package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRequestRepository repo;


    private ItemRequestDtoRequire itemRequestDtoRequire;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse1;
    private ItemRequestDtoResponse itemRequestDtoResponse2;
    private ItemRequest itemRequest;
    private User user;
    private User owner;
    private LocalDateTime created;
    private Item item;
    private ItemDto itemDto;


    @BeforeEach
    void setup() {
        user = new User(1, "Nick", "nick@ya.ru");
        owner = new User(2, "Jack", "jack@ya.ru");

        created = LocalDateTime.now();
        itemRequestDtoRequire = new ItemRequestDtoRequire(1, "description");
        itemRequestDto = new ItemRequestDto(1, "description", user, created);

        itemRequest = new ItemRequest(1, "description", user, created);
        item = new Item(1, "hammer", "tools", true, owner, itemRequest);
        itemDto = ItemMapper.toItemDto(item);
        itemRequestDtoResponse1 = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse2 = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse2.setItems(List.of(itemDto));
    }

    @Test
    void getAllItemRequestByUser_whenUserNotFound() {
        long imaginaryUserId = 500;
        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        assertThrows(NotFoundException.class,
                () -> service.getAllItemRequestByUser(imaginaryUserId));
    }

    @Test
    void getAllItemRequestByUser() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);

        List<ItemRequestDtoResponse> actualItemRequest = service.getAllItemRequestByUser(user.getId());
        List<ItemRequestDtoResponse> expectedItemRequest = List.of();

        assertThat(actualItemRequest).isEqualTo(expectedItemRequest);
    }

    @Test
    void addItemRequest_whenUserNotFound() {
        long imaginaryUserId = 500;
        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        assertThrows(NotFoundException.class,
                () -> service.addItemRequest(imaginaryUserId, itemRequestDtoRequire));
    }

    @Test
    void addItemRequest_whenDescriptionIsNull() {
        itemRequestDtoRequire.setDescription(null);

        assertThrows(BadRequestException.class,
                () -> service.addItemRequest(user.getId(), itemRequestDtoRequire));
    }

    @Test
    void addItemRequest() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(repo.save(ArgumentMatchers.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequest actual = service.addItemRequest(user.getId(), itemRequestDtoRequire);

        assertEquals(itemRequest, actual);
    }

    @Test
    void getItemRequestById_whenUserIsNotFound() {
        long imaginaryUserId = 500;
        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        assertThrows(NotFoundException.class,
                () -> service.getItemRequestById(imaginaryUserId, itemRequest.getId()));
    }

    @Test
    void getItemRequestById_whenItemRequestIsNotFound() {
        long imaginaryItemRequestId = 500;
        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(repo.findById(imaginaryItemRequestId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getItemRequestById(user.getId(), imaginaryItemRequestId));
    }

    @Test
    void getItemRequestById() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(repo.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDtoResponse actual = service.getItemRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDtoResponse1, actual);
    }

    @Test
    void getAllItemRequest_whenPageParamsIsNegative() {
        int from = -2;
        int size = -10;

        assertThrows(BadRequestException.class,
                () -> service.getAllItemRequest(user.getId(), from, size));
    }

    @Test
    void getAllItemRequest_whenUserIsNotFound() {
        long imaginaryUserId = 500;
        int from = 0;
        int size = 10;

        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        assertThrows(NotFoundException.class,
                () -> service.getAllItemRequest(imaginaryUserId, from, size));
    }
}
