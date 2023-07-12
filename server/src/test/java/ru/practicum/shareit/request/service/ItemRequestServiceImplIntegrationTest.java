package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestRepository repo;
    private final ItemRepository itemRepository;
    private final ItemRequestService service;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    private ItemRequestDtoRequire itemRequestDtoRequire;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private ItemRequest itemRequest;
    private User requestor;
    private LocalDateTime created;

    @BeforeEach
    void setup() {
        requestor = new User(0, "Nick", "nick@ya.ru");
        requestor = userService.addUser(requestor);

        created = LocalDateTime.now();
        itemRequestDtoRequire = new ItemRequestDtoRequire(1, "description");
        itemRequestDto = new ItemRequestDto(1, "description", requestor, null);

        itemRequest = new ItemRequest(0, "description", requestor, created);
        itemRequest = repo.save(itemRequest);
        itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        repo.deleteAll();
    }

    @Test
    void getAllItemRequestByUser() {
        List<ItemRequestDtoResponse> actualItemRequestList = service.getAllItemRequestByUser(requestor.getId());

        assertEquals(List.of(itemRequestDtoResponse), actualItemRequestList);
    }

    @Test
    void addItemRequest() {
        ItemRequest savedItemRequest = service.addItemRequest(requestor.getId(), itemRequestDtoRequire);

        long expectedItemRequestId = itemRequest.getId() + 1;

        assertEquals(expectedItemRequestId, savedItemRequest.getId());
        assertEquals(itemRequest.getDescription(), savedItemRequest.getDescription());
        assertEquals(itemRequest.getRequestor(), savedItemRequest.getRequestor());
    }

    @Test
    void getItemRequestById() {
        ItemRequestDtoResponse actualItemRequest = service.getItemRequestById(requestor.getId(), itemRequest.getId());

        assertEquals(itemRequestDtoResponse, actualItemRequest);
    }

    @Test
    void getAllItemRequest() {
        int from = 0;
        int size = 10;

        List<ItemRequestDtoResponse> actualItemRequestList = service.getAllItemRequest(requestor.getId(), from, size);

        assertEquals(List.of(), actualItemRequestList);
    }
}
