package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {
    private int id; // уникальный идентификатор пользователя;
    @NotBlank(message = "Логин не может быть пустым")
    private String name; // имя или логин пользователя;
    @NotNull(message = "Email должен быть указан")
    @Email
    private String email; // адрес электронной почты
}