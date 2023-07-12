package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder(toBuilder = true)
public class ItemDto {

    long id;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
