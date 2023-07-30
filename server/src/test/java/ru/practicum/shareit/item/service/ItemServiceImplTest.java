package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;


    private ItemDto itemDto;
    private Item item;
    private User user;
    private Comment comment;
    private Booking next;
    private Booking last;
    ItemBookingDto itemBookingDto;
    private static final Pageable pageable = PageRequest.of(0, 10);
    private static Page<Item> pageOfItems;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        user = new User(1, "John", "john@ya.ru");
        item = new Item(1, "hammer", "tools", true, user, null);
        itemDto = ItemMapper.toItemDto(item);
        comment = new Comment(1, "cool", item, user, LocalDateTime.now().minusHours(1));

        LocalDateTime lastBookingStart = LocalDateTime.now().minusMonths(2);
        LocalDateTime lastBookingEnd = LocalDateTime.now().minusMonths(1);
        LocalDateTime nextBookingStart = LocalDateTime.now().plusMonths(1);
        LocalDateTime nextBookingEnd = LocalDateTime.now().plusMonths(2);
        last = Booking.builder()
                .id(2)
                .start(lastBookingStart)
                .end(lastBookingEnd)
                .item(item)
                .booker(user)
                .build();
        next = Booking.builder()
                .id(3)
                .start(nextBookingStart)
                .end(nextBookingEnd)
                .item(item)
                .booker(user)
                .build();

        BookingItemDto lastBookingItemDto = BookingMapper.toBookingItemDto(last);
        BookingItemDto nextBookingItemDto = BookingMapper.toBookingItemDto(next);

        pageOfItems = new PageImpl<>(List.of(item), pageable, 1);

        commentDto = new CommentDto(1, "cool", user.getName(), comment.getCreated());
        itemBookingDto = new ItemBookingDto(1, item.getName(), item.getDescription(),
                item.getAvailable(), lastBookingItemDto, nextBookingItemDto, List.of(commentDto));
    }

    @Test
    void getAllItems_whenUserNotFound_throwsException() {
        long imaginaryUserId = 500;
        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        int size = 10;
        PageRequest page = PageRequest.of(0, size);

        assertThrows(NotFoundException.class,
                () -> service.getAllItems(imaginaryUserId, 0, size));

        verify(itemRepository, never()).findAllByOwnerIdOrderById(imaginaryUserId, page);
    }

    @Test
    void getAllItems() {
        int size = 10;

        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(itemRepository.findAllByOwnerIdOrderById(user.getId(), pageable))
                .thenReturn(pageOfItems);
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(anyLong(),
                ArgumentMatchers.any(BookingStatus.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(next));
        when(bookingRepository.findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(anyLong(),
                ArgumentMatchers.any(BookingStatus.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(last));

        List<ItemBookingDto> actualItems = service.getAllItems(user.getId(), 0, size);
        List<ItemBookingDto> expectedItems = List.of(itemBookingDto);

        assertThat(actualItems).isEqualTo(expectedItems);
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(anyLong(),
                ArgumentMatchers.any(BookingStatus.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(next));
        when(bookingRepository.findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(anyLong(),
                ArgumentMatchers.any(BookingStatus.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(last));

        ItemBookingDto actualItem = service.getItemById(1, item.getId());
        ItemBookingDto expectedItem = itemBookingDto;

        assertThat(actualItem).isEqualTo(expectedItem);
    }

    @Test
    void getItemById_whenUserIsNotOwner() {
        User user2 = new User(2, "Nick", "nick@ya.ru");
        Item item2 = new Item(2, "hammer", "tools", true, user, null);

        ItemBookingDto itemBookingDto2 = new ItemBookingDto(2, item.getName(), item.getDescription(),
                item.getAvailable(), null, null, List.of(commentDto));

        when(itemRepository.findById(item2.getId()))
                .thenReturn(Optional.of(item2));
        when(commentRepository.findAllByItemId(item2.getId()))
                .thenReturn(List.of(comment));

        ItemBookingDto actualItem = service.getItemById(user2.getId(), item2.getId());
        ItemBookingDto expectedItem = itemBookingDto2;

        assertThat(actualItem).isEqualTo(expectedItem);
    }

    @Test
    void getItemById_whenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getItemById(1, 500));
    }

    @Test
    void addItem_isBadItem_throwException() {
        var item2 = new Item(2, "", "tools", true, user, null);
        var itemDto2 = ItemMapper.toItemDto(item2);

        assertThrows(BadRequestException.class,
                () -> service.addItem(itemDto2, user.getId()));
    }

    @Test
    void addItem_NotFoundUser_throwException() {
        long imaginaryUserId = 500;
        when(userService.getUserById(imaginaryUserId))
                .thenThrow(new NotFoundException("User by id " + imaginaryUserId + " was not found"));

        assertThrows(NotFoundException.class,
                () -> service.addItem(itemDto, imaginaryUserId));

        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem_isNotFoundItemRequest_throwException() {
        userRepository.save(user);
        long imaginaryItemRequestId = 500;
        itemDto.setRequestId(imaginaryItemRequestId);

        when(itemRequestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addItem(itemDto, user.getId()));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);

        service.addItem(itemDto,user.getId());

        ArgumentCaptor<Item> itemArgumentCaptor =
                ArgumentCaptor.forClass(Item.class);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        var savedItem = itemArgumentCaptor.getValue();

        assertThat(savedItem).isEqualTo(item);
        assertEquals(user, savedItem.getOwner());
        assertNull(savedItem.getRequest());
    }

    @Test
    void updateItem() {
        item.setName("computer");
        Item updated = new Item(1, "computer", "games", true, user, null);
        ItemDto updatedItemDto = ItemMapper.toItemDto(updated);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(itemArgumentCaptor.capture()))
                .thenReturn(item);

        Item actualItem = service.updateItem(1, updatedItemDto, 1);

        assertThat(actualItem).isEqualTo(updated);
        assertEquals(item, itemArgumentCaptor.getValue());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void updateItem_whenOwnerNotFound() {
        int unrealOwnerId = 500;
        item.setName("computer");

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> service.updateItem(item.getId(),
                itemDto, unrealOwnerId));
    }

    @Test
    void search() {
        when(itemRepository.search(anyString(), any()))
                .thenReturn(List.of(item));

        List<ItemDto> actualItems = service.search("some_text", 0, 10);
        List<ItemDto> expectedItems = List.of(itemDto);

        assertThat(actualItems).isEqualTo(expectedItems);
    }

    @Test
    void addComment() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(),
                anyLong(), ArgumentMatchers.any(BookingStatus.class), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(last));
        when(commentRepository.save(ArgumentMatchers.any(Comment.class)))
                .thenReturn(comment);

        Comment actualComment = service.addComment(user.getId(), item.getId(), commentDto);

        assertEquals(1, actualComment.getId());
    }

    @Test
    void addComment_whenItemNotFound() {
        long unrealItemId = 500;

        when(itemRepository.findById(unrealItemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(user.getId(), unrealItemId, commentDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenBookingNotFound() {
        when(userService.getUserById(user.getId()))
                .thenReturn(user);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(),
                anyLong(), any(), any()))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> service.addComment(user.getId(), item.getId(), commentDto));
    }
}