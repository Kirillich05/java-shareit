package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Item item;
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;


    @BeforeEach
    void setup() {
        start = null;
        end = null;
        user1 = new User(1, "John", "john@ya.ru");
        user2 = new User(2, "Nick", "nick@ya.ru");
        item = new Item(1, "hammer", "tools", true, user1, null);
        booking = new Booking(1, start, end, item, user2, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(1, start, end, item.getId(), BookingStatus.WAITING);
        bookingDtoResponse = new BookingDtoResponse(1, start, end, user2, item, BookingStatus.WAITING);
    }


    @SneakyThrows
    @Test
    void addBooking() {
        long userId = 1;
        when(service.addBooking(bookingDtoRequest, userId))
                .thenReturn(booking);

        String actual = mockMvc.perform(post("/bookings")
                        .content(new ObjectMapper().writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), actual);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(service.getBookingById(user2.getId(), booking.getId()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(service, times(1)).getBookingById(user2.getId(), booking.getId());
    }

    @SneakyThrows
    @Test
    void approveBookingStatus() {
        long userId = 1;
        bookingDtoRequest.setStatus(BookingStatus.APPROVED);
        booking.setStatus(BookingStatus.APPROVED);
        bookingDtoResponse.setStatus(BookingStatus.APPROVED);
        when(service.approveBookingStatus(userId, booking.getId(), true))
                .thenReturn(bookingDtoResponse);

        String actual = mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .content(new ObjectMapper().writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), actual);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser() {
        int from = 0;
        int size = 0;
        long userId = 1;

        when(service.getAllBookingsByUser(userId, "ALL", from, size))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllBookingsByUser(userId, "ALL",from, size);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {
        int from = 0;
        int size = 0;
        long userId = 1;

        when(service.getAllBookingsByOwner(userId, "ALL", from, size))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(service, times(1)).getAllBookingsByOwner(userId, "ALL",from, size);
    }
}
