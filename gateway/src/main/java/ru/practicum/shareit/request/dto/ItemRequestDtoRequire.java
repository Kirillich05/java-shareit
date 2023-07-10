package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder(toBuilder = true)
public class ItemRequestDtoRequire {

    long id;

    String description;
}
