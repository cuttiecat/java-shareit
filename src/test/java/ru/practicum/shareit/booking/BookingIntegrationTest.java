package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.userUtils.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    @Test
    void shouldGetOwnerBookingsIntegration() {
        User booker = new User(1L, "Пользователь 1", "email1@mail.ru");
        User owner = new User(2L, "Пользователь 2", "email2@mail.ru");
        Item item = new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null);
        ReceivedBookingDto receivedBookingDto = new ReceivedBookingDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));
        userService.addUser(UserMapper.toUserDto(booker, List.of()));
        userService.addUser(UserMapper.toUserDto(owner, List.of()));
        itemService.addItem(ItemMapper.toItemDto(item, List.of()), 2L);
        bookingService.addBooking(receivedBookingDto, 1L);
        bookingService.setBookingStatus(1L, true, 2L);
        List<ReturnBookingDto> bookingDtoList = bookingService
                .getOwnerBookings(BookingState.ALL, 0, 20, 2L);
        assertEquals(1, bookingDtoList.size());
        assertEquals(1L, bookingDtoList.get(0).getId());
        assertEquals("Пользователь 1", bookingDtoList.get(0).getBooker().getName());
        assertEquals("Предмет 1", bookingDtoList.get(0).getItem().getName());
    }
}
