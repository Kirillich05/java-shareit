package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingItemDto {

    long id;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
    long bookerId;
    long itemId;
    BookingStatus status;
}
