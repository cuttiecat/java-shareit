package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.bookingUtils.BookingStatus;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepositoryImpl bookingRepository;
    @Mock
    private ItemRepositoryImpl itemRepository;
    @Mock
    private UserRepositoryImpl userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User user;
    private Item item;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Пользователь 1", "email1@mail.ru");
        User owner = new User(2L, "Пользователь 2", "email2@mail.ru");
        item = new Item(1L, "Предмет 1", "Описание предмета 1", true, owner, null);
        start = LocalDateTime.now().plusDays(1);
        end = start.plusDays(1);
        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
    }

    @Test
    void shouldAddBooking() {
        ReceivedBookingDto receivedBookingDto = new ReceivedBookingDto(1L, start, end);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        ReturnBookingDto returnBookingDto = bookingService.addBooking(receivedBookingDto, 1L);
        assertNotNull(returnBookingDto);
        assertEquals(1L, returnBookingDto.getId());
        assertEquals("Пользователь 1", returnBookingDto.getBooker().getName());
        assertEquals("Предмет 1", returnBookingDto.getItem().getName());
        verify(bookingRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSetBookingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        ReturnBookingDto returnBookingDto = bookingService.setBookingStatus(1L, true, 2L);
        assertNotNull(returnBookingDto);
        assertEquals(1L, returnBookingDto.getId());
        assertEquals("Пользователь 1", returnBookingDto.getBooker().getName());
        assertEquals("Предмет 1", returnBookingDto.getItem().getName());
        verify(bookingRepository, times(1)).save(any());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        ReturnBookingDto returnBookingDto = bookingService.getBooking(1L, 1L);
        assertNotNull(returnBookingDto);
        assertEquals(1L, returnBookingDto.getId());
        assertEquals("Пользователь 1", returnBookingDto.getBooker().getName());
        assertEquals("Предмет 1", returnBookingDto.getItem().getName());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookerBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllCurrentByBookerId(anyLong(), any(), any())).thenReturn(bookingPage);
        List<ReturnBookingDto> bookingDtoList = bookingService
                .getBookerBookings(BookingState.CURRENT, 0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllPastByBookerId(anyLong(), any(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getBookerBookings(BookingState.PAST,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllFutureByBookerId(anyLong(), any(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getBookerBookings(BookingState.FUTURE,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllWaitingByBookerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getBookerBookings(BookingState.WAITING,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllRejectedByBookerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getBookerBookings(BookingState.REJECTED,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getBookerBookings(BookingState.ALL,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        verify(userRepository, times(6)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllCurrentByBookerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllPastByBookerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllFutureByBookerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllWaitingByBookerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllRejectedByBookerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
    }

    @Test
    void shouldGetOwnerBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllCurrentByOwnerId(anyLong(), any(), any())).thenReturn(bookingPage);
        List<ReturnBookingDto> bookingDtoList = bookingService
                .getOwnerBookings(BookingState.CURRENT, 0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllPastByOwnerId(anyLong(), any(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getOwnerBookings(BookingState.PAST,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllFutureByOwnerId(anyLong(), any(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getOwnerBookings(BookingState.FUTURE,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllWaitingByOwnerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getOwnerBookings(BookingState.WAITING,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllRejectedByOwnerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getOwnerBookings(BookingState.REJECTED,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        when(bookingRepository.findAllByOwnerId(anyLong(), any())).thenReturn(bookingPage);
        bookingDtoList = bookingService.getOwnerBookings(BookingState.ALL,  0, 18, 1L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());

        verify(userRepository, times(6)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllCurrentByOwnerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllPastByOwnerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllFutureByOwnerId(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllWaitingByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllRejectedByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void shouldCheckValidationExceptions() {
        final ReceivedBookingDto receivedBookingDto1 = new ReceivedBookingDto(1L, null, end);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BookingDateValidationException.class,
                () -> bookingService.addBooking(receivedBookingDto1, 1L));

        final ReceivedBookingDto receivedBookingDto2 = new ReceivedBookingDto(1L, start, end);
        item.setAvailable(false);
        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addBooking(receivedBookingDto2, 1L));

        final ReceivedBookingDto receivedBookingDto3 = new ReceivedBookingDto(1L, start.plusYears(1), end);
        item.setAvailable(true);
        assertThrows(BookingDateValidationException.class,
                () -> bookingService.addBooking(receivedBookingDto3, 1L));

        final ReceivedBookingDto receivedBookingDto4 = new ReceivedBookingDto(1L, start, end);
        assertThrows(ItemWithoutOwnerException.class,
                () -> bookingService.addBooking(receivedBookingDto4, 2L));

        assertThrows(UserExistException.class,
                () -> bookingService.getBooking(1L, 4L));

        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(BookingChangeStatusException.class,
                () -> bookingService.setBookingStatus(1L, true, 2L));

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(ItemWithWrongOwner.class,
                () -> bookingService.setBookingStatus(1L, true, 1L));
    }

    @Test
    void shouldCheckExistExceptions() {
        final ReceivedBookingDto receivedBookingDto1 = new ReceivedBookingDto(1L, start, end);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemExistException.class,
                () -> bookingService.addBooking(receivedBookingDto1, 1L));

        final ReceivedBookingDto receivedBookingDto2 = new ReceivedBookingDto(1L, start, end);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserExistException.class,
                () -> bookingService.addBooking(receivedBookingDto2, 1L));
        assertThrows(UserExistException.class,
                () -> bookingService.getBooking(1L, 1L));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingExistException.class,
                () -> bookingService.getBooking(1L, 1L));
        assertThrows(BookingExistException.class,
                () -> bookingService.setBookingStatus(1L, true, 2L));
    }
}