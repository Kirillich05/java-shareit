package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {

    long id;
    String text;
    String authorName;
    LocalDateTime created;
}
