package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.repository.CommentRepositoryImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepositoryImpl userRepository;
    @Mock
    private CommentRepositoryImpl commentRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserDto receivedUser;
    private User returnUser;

    @BeforeEach
    void setUp() {
        receivedUser = new UserDto(null, "Пользователь 1", "email1@mail.ru", List.of());
        returnUser = new User(1L, "Пользователь 1", "email1@mail.ru");
    }

    @Test
    void shouldAddUser() {
        when(userRepository.save(any())).thenReturn(returnUser);
        UserDto testUser = userService.addUser(receivedUser);
        assertNotNull(testUser);
        assertEquals(1L, testUser.getId());
        assertEquals("Пользователь 1", testUser.getName());
        assertEquals("email1@mail.ru", testUser.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateUser() {
        receivedUser.setId(1L);
        receivedUser.setName("Новый пользователь 1");
        returnUser.setName("Новый пользователь 1");
        when(userRepository.save(any())).thenReturn(returnUser);
        when(userRepository.findById(any())).thenReturn(Optional.of(returnUser));
        UserDto testUser = userService.updateUser(1L, receivedUser);
        assertNotNull(testUser);
        assertEquals(1L, testUser.getId());
        assertEquals("Новый пользователь 1", testUser.getName());
        assertEquals("email1@mail.ru", testUser.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldGetUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(returnUser));
        UserDto testUser = userService.getUser(1L);
        assertNotNull(testUser);
        assertEquals(1L, testUser.getId());
        assertEquals("Пользователь 1", testUser.getName());
        assertEquals("email1@mail.ru", testUser.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetUsers() {
        User returnUser2 = new User(5L, "Пользователь 5", "email5@mail.ru");
        User returnUser3 = new User(9L, "Пользователь 9", "email9@mail.ru");
        when(userRepository.findAll(new ShareItPageable(0, 20, Sort.unsorted())))
                .thenReturn(new PageImpl<>(List.of(returnUser,returnUser2, returnUser3)));
        when(commentRepository.findAll()).thenReturn(List.of());
        List<UserDto> userList = userService.getUsers(null, null);
        assertEquals(3, userList.size());
        assertEquals(1L, userList.get(0).getId());
        assertEquals(9L, userList.get(2).getId());
        assertEquals(5L, userList.get(1).getId());
        verify(userRepository, times(1)).findAll(new ShareItPageable(0, 20, Sort.unsorted()));
    }
}