package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository repository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setup() {
        user = new User(0, "John", "john@ya.ru");
        userRepository.save(user);

        item1 = new Item(0, "hammer", "tools", true, user, null);
        item2 = new Item(0, "book", "java", true, user, null);
        repository.save(item1);
        repository.save(item2);

        itemRequest = new ItemRequest(0, "item description", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

    }

    @Test
    void search() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> actual1 = repository.search("hammer", pageable);
        List<Item> expected1 = List.of(item1);

        List<Item> actual2 = repository.search("book", pageable);
        List<Item> expected2 = List.of(item2);

        List<Item> actual3 = repository.search("something unreal", pageable);
        List<Item> expected3 = List.of();

        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
