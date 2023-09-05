package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private static final String CONTROLLER_LOG = "Контроллер пользователей получил запрос на {}{}";

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info(CONTROLLER_LOG, "Добавление пользователя: ", userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        log.info(CONTROLLER_LOG, "Обновление пользователя c id: ", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        log.info(CONTROLLER_LOG, "Удаление пользователя с id: ", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long userId) {
        log.info(CONTROLLER_LOG, "Получение пользователя с id: ", userId);
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                  @Positive @RequestParam(value = "size", required = false) Integer size) {
        log.info(CONTROLLER_LOG, "Получение всех пользователей", "");
        return userService.getUsers(from, size);
    }
}