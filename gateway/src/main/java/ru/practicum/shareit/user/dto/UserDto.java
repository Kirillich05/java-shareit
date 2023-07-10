package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder(toBuilder = true)
public class UserDto {

    long id;

    String name;


    @Email
    String email;
}
