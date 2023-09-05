package ru.practicum.shareit.user.userUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user, List<CommentDto> comments) {
        log.info("Начата процедура преобразования пользователя в ДТО: {}", user);
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                comments);
    }

    public User toUser(UserDto userDto) {
        log.info("Начата процедура преобразования ДТО в пользователя: {}", userDto);
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}
