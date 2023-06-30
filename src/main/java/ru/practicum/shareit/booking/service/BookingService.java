package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDtoResponse getBookingById(long userId, long bookingId);

    BookingDtoResponse approveBookingStatus(long userId, long bookingId, boolean approved);

    List<BookingDtoResponse> getAllBookingsByUser(long userId, String state);

    List<BookingDtoResponse> getAllBookingsByOwner(long userId, String state);
}
