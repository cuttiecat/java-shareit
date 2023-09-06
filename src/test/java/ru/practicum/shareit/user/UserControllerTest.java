package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto receivedUserDto;
    private UserDto returnUserDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        receivedUserDto = new UserDto(null, "Пользователь 1", "email1@mail.ru", List.of());
        returnUserDto = receivedUserDto;
        returnUserDto.setId(1L);
    }

    @Test
    void shouldAddUser() throws Exception {
        when(userService.addUser(any())).thenReturn(returnUserDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(receivedUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Пользователь 1")))
                .andExpect(jsonPath("$.email", is("email1@mail.ru")));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        returnUserDto.setName("Новый пользователь 1");
        when(userService.updateUser(anyLong(), any())).thenReturn(returnUserDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(returnUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Новый пользователь 1")))
                .andExpect(jsonPath("$.email", is("email1@mail.ru")));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUser() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(returnUserDto);
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Пользователь 1")))
                .andExpect(jsonPath("$.email", is("email1@mail.ru")));
    }

    @Test
    void shouldGetUsers() throws Exception {
        when(userService.getUsers(0, 7)).thenReturn(List.of(returnUserDto));
        mvc.perform(get("/users?from=0&size=7")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Пользователь 1")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("email1@mail.ru")));
    }
}