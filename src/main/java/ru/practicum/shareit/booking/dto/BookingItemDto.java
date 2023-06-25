package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemDto {

    long id;
    LocalDateTime start;
    LocalDateTime end;
    long bookerId;
    long itemId;
    BookingStatus status;
}
