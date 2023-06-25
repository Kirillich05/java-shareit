package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {


    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        return Booking.builder()
                .id(bookingDtoRequest.getId())
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDtoRequest.getStatus())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStatus()
        );
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStatus()
        );
    }
}
