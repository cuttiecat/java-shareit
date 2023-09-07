package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {

        log.info("Добавлен пользователь {} ", userDto.getId());
        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                              @PathVariable Long userId) {

        log.info("Данные пользователя обновлены {} ", userDto.getId());
        return ResponseEntity.ok(userService.updateUser(userDto, userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {

        log.info("Пользователь {} удалён ", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {

        log.info("Получение запроса об пользователе {} ", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {

        log.info("Список всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
   }
}
