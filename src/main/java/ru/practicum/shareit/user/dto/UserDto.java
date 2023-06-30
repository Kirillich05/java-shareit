package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    long id;

    String name;

    @Email
    String email;
}
