package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.RequestExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private RequestRepositoryImpl requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepositoryImpl itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    private Request request;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Пользователь 1", "email1@mail.ru");
        request = new Request(1L, "Описание запроса 1", user, LocalDateTime.now());
        User owner = new User(2L, "Пользователь 2", "email2@mail.ru");
        item = new Item(1L, "Предмет 1", "Описание предмета 1", true, owner, request);
    }

    @Test
    void shouldAddRequest() {
        ReceivedRequestDto receivedRequestDto = new ReceivedRequestDto("Описание запроса 1");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);
        ReturnRequestDto returnRequestDto = requestService.addRequest(receivedRequestDto, 1L);
        assertNotNull(returnRequestDto);
        assertEquals(1L, returnRequestDto.getId());
        assertEquals("Описание запроса 1", returnRequestDto.getDescription());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void shouldGetOthersRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllForUser(1L, new ShareItPageable(0, 20,
                Sort.by("created").descending())))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(itemRepository.findAllByRequestsId(List.of(1L))).thenReturn(List.of(item));
        List<ReturnRequestDto> requestDtoList = requestService.getOthersRequests(0, 20, 1L);
        assertEquals(1, requestDtoList.size());
        assertEquals(1L, requestDtoList.get(0).getId());
        assertEquals("Описание запроса 1", requestDtoList.get(0).getDescription());
        verify(itemRepository, times(1)).findAllByRequestsId(any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllForUser(anyLong(), any());
    }

    @Test
    void shouldGetRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestsId(any())).thenReturn(List.of(item));
        ReturnRequestDto returnRequestDto = requestService.getRequest(1L, 2L);
        assertNotNull(returnRequestDto);
        assertEquals(1L, returnRequestDto.getId());
        assertEquals("Описание запроса 1", returnRequestDto.getDescription());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByRequestsId(any());
    }

    @Test
    void shouldGetUserRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findRequestsByRequestorId(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestsId(any())).thenReturn(List.of(item));
        List<ReturnRequestDto> requestDtoList = requestService.getUserRequests(1L);
        assertEquals(1, requestDtoList.size());
        assertEquals(1L, requestDtoList.get(0).getId());
        assertEquals("Описание запроса 1", requestDtoList.get(0).getDescription());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findRequestsByRequestorId(anyLong());
        verify(itemRepository, times(1)).findAllByRequestsId(any());
    }

    @Test
    void checkExistException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RequestExistException.class, () -> requestService.getRequest(50L, 1L));
    }
}