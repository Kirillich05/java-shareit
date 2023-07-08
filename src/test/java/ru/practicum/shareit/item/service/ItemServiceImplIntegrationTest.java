package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private ItemService service;

    private final ItemRepository itemRepo;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemDto itemDto;
    private Comment comment;
    private Item item;
    BookingDtoRequest bookingDtoRequest;


    @BeforeEach
    public void setup() {
        user = new User(0, "John", "john@ya.ru");
        userRepository.save(user);
        item = new Item(0, "hammer", "tools", true, user, null);
        itemDto = ItemMapper.toItemDto(item);
        comment = new Comment(0, "cool", item, user, LocalDateTime.now().minusHours(1));

        LocalDateTime lastBookingStart = LocalDateTime.now().minusMonths(2);
        LocalDateTime lastBookingEnd = LocalDateTime.now().minusMonths(1);
        bookingDtoRequest = new BookingDtoRequest(0, lastBookingStart, lastBookingEnd, item.getId(), null);

        service = new ItemServiceImpl(itemRepo, userService, bookingRepository,
                commentRepository, itemRequestRepository);

    }

    @Test
    void getAllItems() {
        service.addItem(itemDto, user.getId());

        List<ItemBookingDto> savedItems = service.getAllItems(user.getId(), 0, 10);
        ItemBookingDto savedItem = savedItems.get(0);

        assertThat(savedItem).isNotNull();
        assertEquals(1, savedItems.size());
    }

    @Test
    void addItem() {
        Item savedItem = service.addItem(itemDto, user.getId());

        Optional<Item> actualItem = itemRepo.findById(savedItem.getId());

        assertTrue(actualItem.isPresent());
        assertThat(actualItem.get()).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        Item savedItem = service.addItem(itemDto, user.getId());

        savedItem.setName("computer");
        service.updateItem(savedItem.getId(), ItemMapper.toItemDto(savedItem), user.getId());
        ItemBookingDto foundItem = service.getItemById(user.getId(), savedItem.getId());

        assertThat(foundItem.getName()).isEqualTo("computer");
    }

    @Test
    void getItemById() {
        Item savedItem = service.addItem(itemDto, user.getId());
        ItemBookingDto foundExpectedItem = service.getItemById(user.getId(), savedItem.getId());

        Optional<Item> actualItem = itemRepo.findById(savedItem.getId());
        ItemBookingDto actualItemBookingDto = ItemMapper.toItemBookingDto(actualItem.get());

        assertThat(actualItemBookingDto).isEqualTo(foundExpectedItem);
    }

    @Test
    void search() {
        Item expectedItem = service.addItem(itemDto, user.getId());
        int from = 0;
        int size = 10;
        String searchedText = "hammer";
        ItemDto expectedItemDto = ItemMapper.toItemDto(expectedItem);

        List<ItemDto> actualSearchedItem = service.search(searchedText, from, size);
        List<ItemDto> expectedSearchedItem = List.of(expectedItemDto);

        assertThat(actualSearchedItem).isEqualTo(expectedSearchedItem);
    }
}
