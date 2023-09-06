package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.userUtils.UserMapper;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {
    private final UserServiceImpl userService;

    @Test
    void shouldGetUsersIntegration() {
        User user1 = new User(1L, "Пользователь 1", "email1@mail.ru");
        User user2 = new User(2L, "Пользователь 2", "email2@mail.ru");
        userService.addUser(UserMapper.toUserDto(user1, List.of()));
        userService.addUser(UserMapper.toUserDto(user2, List.of()));
        List<UserDto> userDtoList = userService.getUsers(0, 20);
        userDtoList.sort(Comparator.comparing(UserDto::getId));

        assertEquals(2, userDtoList.size());
        assertEquals(1L, userDtoList.get(0).getId());
        assertEquals("Пользователь 1", userDtoList.get(0).getName());
        assertEquals("email1@mail.ru", userDtoList.get(0).getEmail());
        assertEquals(2L, userDtoList.get(1).getId());
        assertEquals("Пользователь 2", userDtoList.get(1).getName());
        assertEquals("email2@mail.ru", userDtoList.get(1).getEmail());
    }
}