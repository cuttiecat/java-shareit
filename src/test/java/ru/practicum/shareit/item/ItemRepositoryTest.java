package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private ItemRepositoryImpl itemRepository;

    @Autowired
    private RequestRepositoryImpl requestRepository;

    @Test
    void shouldFindAllByOwnerId() {
        User requestor = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        Request request = requestRepository
                .save(new Request(1L, "Описание запроса №1", requestor, LocalDateTime.now()));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, request));
        List<Item> itemList = itemRepository
                .findAllByOwnerId(2L, new ShareItPageable(0, 5, Sort.unsorted())).toList();
        assertEquals(1, itemList.size());
        assertEquals(1L, itemList.get(0).getId());
        assertEquals("Предмет №1", itemList.get(0).getName());
        assertEquals("Описание предмета №1", itemList.get(0).getDescription());
        assertEquals("Пользователь №2", itemList.get(0).getOwner().getName());
        assertEquals("Описание запроса №1", itemList.get(0).getRequest().getDescription());
    }

    @Test
    void shouldFindAllByText() {
        User requestor = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        Request request = requestRepository
                .save(new Request(1L, "Описание запроса №1", requestor, LocalDateTime.now()));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, request));
        List<Item> itemList = itemRepository
                .findAllByText("реД", new ShareItPageable(0, 5, Sort.unsorted())).toList();
        assertEquals(1, itemList.size());
        assertEquals(1L, itemList.get(0).getId());
        assertEquals("Предмет №1", itemList.get(0).getName());
        assertEquals("Описание предмета №1", itemList.get(0).getDescription());
        assertEquals("Пользователь №2", itemList.get(0).getOwner().getName());
        assertEquals("Описание запроса №1", itemList.get(0).getRequest().getDescription());
    }

    @Test
    void shouldFindAllByRequestsId() {
        User requestor = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        Request request = requestRepository
                .save(new Request(1L, "Описание запроса №1", requestor, LocalDateTime.now()));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, request));
        List<Item> itemList = itemRepository.findAllByRequestsId(List.of(1L));
        assertEquals(1, itemList.size());
        assertEquals(1L, itemList.get(0).getId());
        assertEquals("Предмет №1", itemList.get(0).getName());
        assertEquals("Описание предмета №1", itemList.get(0).getDescription());
        assertEquals("Пользователь №2", itemList.get(0).getOwner().getName());
        assertEquals("Описание запроса №1", itemList.get(0).getRequest().getDescription());
    }
}
