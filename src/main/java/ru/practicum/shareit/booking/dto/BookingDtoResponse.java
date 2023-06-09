package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingDtoResponse {

    long id;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
    User booker;
    Item item;
    BookingStatus status;
}
