package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
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
public class RequestIntegrationTest {
    private final RequestServiceImpl requestService;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Test
    void shouldGetUserRequestsIntegration() {
        User requestor = new User(1L, "Пользователь №1", "email1@mail.ru");
        User owner = new User(2L, "Пользователь №2", "email2@mail.ru");
        Item item1 = new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, new Request(1L, null, null, null));
        Item item2 = new Item(2L, "Предмет №2", "Описание предмета №2",
                true, owner, new Request(2L, null, null, null));
        userService.addUser(UserMapper.toUserDto(requestor, List.of()));
        userService.addUser(UserMapper.toUserDto(owner, List.of()));
        requestService.addRequest(new ReceivedRequestDto("Описание запроса №1"), 1L);
        requestService.addRequest(new ReceivedRequestDto("Описание запроса №2"), 1L);
        itemService.addItem(ItemMapper.toItemDto(item1, List.of()), 2L);
        itemService.addItem(ItemMapper.toItemDto(item2, List.of()), 2L);

        List<ReturnRequestDto> requestDtoList = requestService.getUserRequests(1L);
        requestDtoList.sort(Comparator.comparing(ReturnRequestDto::getId));
        assertEquals(2, requestDtoList.size());
        assertEquals(1L, requestDtoList.get(0).getId());
        assertEquals("Описание запроса №1", requestDtoList.get(0).getDescription());
        assertEquals(1, requestDtoList.get(0).getItems().size());
        assertEquals("Предмет №1", requestDtoList.get(0).getItems().get(0).getName());
        assertEquals(2L, requestDtoList.get(1).getId());
        assertEquals("Описание запроса №2", requestDtoList.get(1).getDescription());
        assertEquals(1, requestDtoList.get(1).getItems().size());
        assertEquals("Предмет №2", requestDtoList.get(1).getItems().get(0).getName());
    }
}
