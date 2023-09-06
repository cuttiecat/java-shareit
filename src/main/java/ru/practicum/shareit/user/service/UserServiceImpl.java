package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserExistException;
import ru.practicum.shareit.item.itemUtils.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepositoryImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.userUtils.UserMapper;
import ru.practicum.shareit.utils.ShareItPageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;
    private final CommentRepositoryImpl commentRepository;
    private static final String SERVICE_LOG = "{}{} - запрос: ";

    public UserDto addUser(UserDto userDto) {
        log.info(SERVICE_LOG, "Добавление пользователя: ", userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)), List.of());
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info(SERVICE_LOG, "Обновление пользователя c id: ", userId);
        User currentUser = checkUserExist(userId);
        userDto.setId(userId);
        if (userDto.getName() == null) {
            userDto.setName(currentUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(currentUser.getEmail());
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user),
                commentRepository.findAllByAuthorId(userId).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
    }

    public void deleteUser(Long userId) {
        log.info(SERVICE_LOG, "Удаление пользователя с id: ", userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        log.info(SERVICE_LOG, "Получение пользователя с id: ", userId);
        User user = checkUserExist(userId);
        return UserMapper.toUserDto(user, commentRepository.findAllByAuthorId(userId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Integer from, Integer size) {
        log.info(SERVICE_LOG, "Получение всех пользователей", "");
        Map<Long, List<Comment>> commentMap = commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(comment -> comment.getAuthor().getId()));
        return userRepository.findAll(ShareItPageable.checkPageable(from, size, Sort.unsorted())).stream()
                .map(user -> UserMapper.toUserDto(user, commentMap.getOrDefault(user.getId(), List.of()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private User checkUserExist(Long ownerId) {
        log.info("Начата процедура проверки наличия в репозитории пользователя с id: {}", ownerId);
        return userRepository.findById(ownerId).orElseThrow(
                () -> new UserExistException("Ошибка. Запрошенного пользователя в базе данных не существует"));
    }
}