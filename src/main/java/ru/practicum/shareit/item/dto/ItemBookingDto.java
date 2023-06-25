package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {

    long id;
    String name;
    String description;
    Boolean available;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;
}
