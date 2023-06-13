package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {

    @PositiveOrZero
    long id;

    @NotNull
    String name;

    @NotNull
    String description;

    @NotNull
    Boolean available;

    @NotNull
    long owner;

    ItemRequest request;
}
