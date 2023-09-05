package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {
    private int id;
    @NotBlank(message = "Логин должен быть указан")
    private String name;
    @NotNull(message = "Email должен быть указан")
    @Email
    private String email;
}
