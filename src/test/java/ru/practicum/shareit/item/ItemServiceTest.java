package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.bookingUtils.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemUtils.CommentMapper;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryImpl;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepositoryImpl itemRepository;
    @Mock
    private UserRepositoryImpl userRepository;
    @Mock
    private BookingRepositoryImpl bookingRepository;
    @Mock
    private CommentRepositoryImpl commentRepository;
    @Mock
    private RequestRepositoryImpl requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Request request;
    private Item item;
    private User owner;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Пользователь №1", "email1@mail.ru");
        request = new Request(1L, "Описание запроса №1", user, LocalDateTime.now());
        owner = new User(2L, "Пользователь №2", "email2@mail.ru");
        item = new Item(1L, "Предмет №1", "Описание предмета №1", true, owner, request);
    }

    @Test
    void shouldAddItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = itemService.addItem(ItemMapper.toItemDto(item, List.of()), 2L);
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Предмет №1", itemDto.getName());
        assertEquals("Описание предмета №1", itemDto.getDescription());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = itemService.updateItem(1L, ItemMapper.toItemDto(item, List.of()), 2L);
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Предмет №1", itemDto.getName());
        assertEquals("Описание предмета №1", itemDto.getDescription());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(2)).findById(anyLong());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        itemService.deleteItem(1L, 2L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldGetItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());
        when(bookingRepository.findNearPreviousBooking(any(), anyLong(), anyLong()))
                .thenReturn(List.of(new BookingItemDto(1L, 1L, 1L)));
        when(bookingRepository.findNearNextBooking(any(), anyLong(), anyLong()))
                .thenReturn(List.of(new BookingItemDto(2L, 1L, 1L)));
        ItemDto itemDto = itemService.getItem(1L, 2L);
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Предмет №1", itemDto.getName());
        assertEquals("Описание предмета №1", itemDto.getDescription());
        assertEquals(1L, itemDto.getLastBooking().getId());
        assertEquals(2L, itemDto.getNextBooking().getId());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findNearPreviousBooking(any(), anyLong(), anyLong());
        verify(bookingRepository, times(1)).findNearNextBooking(any(), anyLong(), anyLong());
    }

    @Test
    void shouldGetItemsByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L, new ShareItPageable(0, 20, Sort.unsorted())))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findAllByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bookingRepository.findNearPreviousBookings(any(), anyLong())).thenReturn(List.of());
        when(bookingRepository.findNearNextBookings(any(), anyLong())).thenReturn(List.of());
        when(commentRepository.findAllByItemOwnerId(any())).thenReturn(List.of());

        List<ItemDto> itemDtoList = itemService.getItemsByOwner(2L, 0, 20);
        assertEquals(1, itemDtoList.size());
        assertEquals(1L, itemDtoList.get(0).getId());
        assertEquals("Предмет №1", itemDtoList.get(0).getName());
        assertEquals("Описание предмета №1", itemDtoList.get(0).getDescription());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findNearPreviousBookings(any(), anyLong());
        verify(bookingRepository, times(1)).findNearNextBookings(any(), anyLong());
        verify(commentRepository, times(1)).findAllByItemOwnerId(any());
    }

    @Test
    void shouldGetItemsByName() {
        when(itemRepository.findAllByText("Текст", new ShareItPageable(0, 20, Sort.unsorted())))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> itemDtoList = itemService.getItemsByName("Текст", 0, 20);
        assertEquals(1, itemDtoList.size());
        assertEquals(1L, itemDtoList.get(0).getId());
        assertEquals("Предмет №1", itemDtoList.get(0).getName());
        assertEquals("Описание предмета №1", itemDtoList.get(0).getDescription());
        verify(itemRepository, times(1)).findAllByText(anyString(), any());
    }

    @Test
    void shouldAddCommentToItem() {
        LocalDateTime localDateTime = LocalDateTime.now();
        User user = new User(3L, "Пользователь №3", "email3@mail.ru");
        Comment comment = new Comment(1L, "Текст комментария", item, user, localDateTime);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllPastByBookerId(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking(1L, localDateTime.minusDays(4),
                        localDateTime.minusDays(2), item, user, BookingStatus.APPROVED))));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addCommentToItem(1L, CommentMapper.toCommentDto(comment), 3L);

        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Текст комментария", commentDto.getText());
        assertEquals("Пользователь №3", commentDto.getAuthorName());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllPastByBookerId(anyLong(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void checkExistExceptions() {
        final ItemDto itemDto1 = new ItemDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserExistException.class,
                () -> itemService.addItem(itemDto1, 2L));
    }

    @Test
    void checkValidationException() {
        Item localItem = new Item(2L, null, null, null, null, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(),
                item, owner, BookingStatus.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(localItem));
        when(bookingRepository.findAllPastByBookerId(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        assertThrows(CommentBookerException.class, () -> itemService.addCommentToItem(
                2L, new CommentDto(null, "Комментарий №1", null, null),2L));
    }
}