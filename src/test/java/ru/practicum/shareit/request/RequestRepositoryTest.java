package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private RequestRepositoryImpl requestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindRequestsByRequestorId() {
        User user = userRepository.save(new User(1L, "Пользователь 1", "email1@mail.ru"));
        requestRepository.save(new Request(1L, "Описание запроса 1", user, LocalDateTime.now()));
        List<Request> requestList = requestRepository.findRequestsByRequestorId(1L);
        assertEquals(1, requestList.size());
        assertEquals(1L, requestList.get(0).getId());
        assertEquals("Описание запроса 1", requestList.get(0).getDescription());
        assertEquals("Пользователь 1", requestList.get(0).getRequestor().getName());
    }

    @Test
    void shouldFindAllForUser() {
        User user = userRepository.save(new User(1L, "1 Пользователь", "1email@mail.ru"));
        requestRepository.save(new Request(1L, "1 Описание запроса", user, LocalDateTime.now()));
        List<Request> requestList = requestRepository
                .findAllForUser(2L, new ShareItPageable(0, 5, Sort.unsorted())).toList();
        assertEquals(1, requestList.size());
        assertEquals(1L, requestList.get(0).getId());
        assertEquals("1 Описание запроса", requestList.get(0).getDescription());
        assertEquals("1 Пользователь", requestList.get(0).getRequestor().getName());
    }
}