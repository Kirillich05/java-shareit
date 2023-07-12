package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingItemDto {

    long id;
    LocalDateTime start;
    LocalDateTime end;
    long bookerId;
    long itemId;
    BookingStatus status;
}
