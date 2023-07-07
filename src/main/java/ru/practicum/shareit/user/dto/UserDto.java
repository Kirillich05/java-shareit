package ru.practicum.shareit.user.dto;

import lombok.*;
import org.hibernate.validator.constraints.Email;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {

    long id;

    String name;

    @Email
    String email;
}
