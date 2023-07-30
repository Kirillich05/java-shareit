package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl service;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private Item item;
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private static final Pageable pageable = PageRequest.of(0, 10);
    private static Page<Booking> bookingPage;

    @BeforeEach
    void setup() {
        start = LocalDateTime.now().plusHours(2);
        end = LocalDateTime.now().plusDays(1);
        user1 = new User(1, "John", "john@ya.ru");
        user2 = new User(2, "Nick", "nick@ya.ru");
        item = new Item(1, "hammer", "tools", true, user1, null);
        booking = new Booking(1, start, end, item, user2, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(1, start, end, item.getId(), BookingStatus.WAITING);
        bookingPage = new PageImpl<>(List.of(booking), pageable, 1);
    }

    @Test
    void addBooking() {
        long userId = user2.getId();

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        service.addBooking(bookingDtoRequest, userId);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        var savedBooking = bookingArgumentCaptor.getValue();

        assertThat(savedBooking).isEqualTo(booking);
        assertEquals(user2, savedBooking.getBooker());
    }


    @Test
    void addBooking_whenNotFoundUser() {
        long imaginaryUserId = 500;

        assertThrows(NotFoundException.class,
                () -> service.addBooking(bookingDtoRequest, imaginaryUserId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenNotFoundItem() {
        long userId = user2.getId();
        long imaginaryItemId = 500;
        bookingDtoRequest.setItemId(imaginaryItemId);

        when(itemRepository.findById(imaginaryItemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addBooking(bookingDtoRequest, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenUserIsBooker() {
        long userId = user1.getId();

        when(userService.getUserById(userId))
                .thenReturn(user1);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> service.addBooking(bookingDtoRequest, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenItemIsNotBookingAvailable() {
        long userId = user2.getId();
        item.setAvailable(false);

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class,
                () -> service.addBooking(bookingDtoRequest, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenBookingIsNotValid() {
        long userId = user2.getId();
        bookingDtoRequest.setStart(null);

        assertThrows(BadRequestException.class,
                () -> service.addBooking(bookingDtoRequest, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById() {
        long userId = user2.getId();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse actual = service.getBookingById(userId, bookingDtoRequest.getId());
        BookingDtoResponse expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBookingById_whenUserIsNotBooker() {
        long imaginaryUserId = 500;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> service.getBookingById(imaginaryUserId, booking.getId()));
    }

    @Test
    void getBookingById_whenNotFoundBooking() {
        long imaginaryBookingId = 500;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getBookingById(user2.getId(), imaginaryBookingId));
    }

    @Test
    void approveBookingStatus() {
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse actual = service.approveBookingStatus(user1.getId(), bookingDtoRequest.getId(), true);
        BookingDtoResponse expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.APPROVED);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void approveBookingStatus_whenBookingIsNotFound() {
        long imaginaryBookingId = 500;

        when(bookingRepository.findById(imaginaryBookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.approveBookingStatus(user1.getId(), imaginaryBookingId, true));
    }

    @Test
    void approveBookingStatus_whenUserIsNotItemOwner() {
        long imaginaryUserId = 500;

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> service.approveBookingStatus(imaginaryUserId, bookingDtoRequest.getId(), true));
    }

    @Test
    void approveBookingStatus_whenStatusIsApproved() {
        bookingDtoRequest.setStatus(BookingStatus.APPROVED);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> service.approveBookingStatus(user1.getId(), bookingDtoRequest.getId(), true));
    }

    @Test
    void approveBookingStatus_setRejectedStatus() {
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse actual = service.approveBookingStatus(user1.getId(), bookingDtoRequest.getId(), false);
        BookingDtoResponse expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.REJECTED);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBookingsByUser_whenStateIsAll() {
        long userId = user2.getId();
        String state = "ALL";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(user2, pageable))
                .thenReturn(bookingPage);


        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenFromIsNegative() {
        long userId = user2.getId();
        String state = "ALL";
        int from = -5;

        when(userService.getUserById(userId))
                .thenReturn(user2);

        assertThrows(BadRequestException.class,
                () -> service.getAllBookingsByUser(userId, state, from, 10));
    }

    @Test
    void getAllBookingsByUser_whenStateIsPast() {
        long userId = user2.getId();
        String state = "PAST";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenStateIsCurrent() {
        long userId = user2.getId();
        String state = "CURRENT";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenStateIsFuture() {
        long userId = user2.getId();
        String state = "FUTURE";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenStateIsRejected() {
        long userId = user2.getId();
        String state = "REJECTED";
        booking.setStatus(BookingStatus.REJECTED);

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED, pageable))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.REJECTED);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenStateIsWaiting() {
        long userId = user2.getId();
        String state = "WAITING";
        booking.setStatus(BookingStatus.WAITING);

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING, pageable))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actual = service.getAllBookingsByUser(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByUser_whenUnknownState() {
        long userId = user2.getId();
        String state = "UNKNOWN";

        when(userService.getUserById(userId))
                .thenReturn(user2);

        assertThrows(ValidationException.class,
                () -> service.getAllBookingsByUser(userId, state, 0, 10));
    }





    @Test
    void getAllBookingsByOwner_whenStateIsAll() {
        long userId = user2.getId();
        String state = "ALL";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(user2, pageable))
                .thenReturn(bookingPage);


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByOwner_whenFromIsNegative() {
        long userId = user2.getId();
        String state = "ALL";
        int from = -5;

        when(userService.getUserById(userId))
                .thenReturn(user2);

        assertThrows(BadRequestException.class,
                () -> service.getAllBookingsByOwner(userId, state, from, 10));
    }

    @Test
    void getAllBookingsByOwner_whenUnknownState() {
        long userId = user2.getId();
        String state = "UNKNOWN";

        when(userService.getUserById(userId))
                .thenReturn(user2);

        assertThrows(ValidationException.class,
                () -> service.getAllBookingsByOwner(userId, state, 0, 10));
    }

    @Test
    void getAllBookingsByOwner_whenStateIsPast() {
        long userId = user2.getId();
        String state = "PAST";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingPage);


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByOwner_whenStateIsCurrent() {
        long userId = user2.getId();
        String state = "CURRENT";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(bookingPage);


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByOwner_whenStateIsFuture() {
        long userId = user2.getId();
        String state = "FUTURE";

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingPage);


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByOwner_whenStateIsRejected() {
        long userId = user2.getId();
        String state = "REJECTED";
        booking.setStatus(BookingStatus.REJECTED);

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.REJECTED, pageable))
                .thenReturn(List.of(booking));


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.REJECTED);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }

    @Test
    void getAllBookingsByOwner_whenStateIsWaiting() {
        long userId = user2.getId();
        String state = "WAITING";
        booking.setStatus(BookingStatus.WAITING);

        when(userService.getUserById(userId))
                .thenReturn(user2);
        when(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.WAITING, pageable))
                .thenReturn(List.of(booking));


        List<BookingDtoResponse> actual = service.getAllBookingsByOwner(user2.getId(), state, 0, 10);
        BookingDtoResponse  expected = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
        List<BookingDtoResponse> expectedList = List.of(expected);

        assertThat(actual).isEqualTo(expectedList);
    }
}
