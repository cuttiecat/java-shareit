package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.userUtils.UserMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemIntegrationTest {
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    @Test
    void shouldGetItemsByOwnerIntegration() {
        User booker = new User(1L, "Пользователь 1", "email1@mail.ru");
        User owner = new User(2L, "Пользователь 2", "email2@mail.ru");
        Item item1 = new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null);
        Item item2 = new Item(2L, "Предмет 2", "Описание предмета 2",
                true, owner, null);
        ReceivedBookingDto receivedBookingDto = new ReceivedBookingDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));
        userService.addUser(UserMapper.toUserDto(booker, List.of()));
        userService.addUser(UserMapper.toUserDto(owner, List.of()));
        itemService.addItem(ItemMapper.toItemDto(item1, List.of()), 2L);
        itemService.addItem(ItemMapper.toItemDto(item2, List.of()), 2L);
        bookingService.addBooking(receivedBookingDto, 1L);
        bookingService.setBookingStatus(1L, true, 2L);
        itemService.addCommentToItem(1L, new CommentDto(1L, "Комментарий 1",
                null, null), 1L);

        List<ItemDto> itemDtoList = itemService.getItemsByOwner(2L, 0, 4);
        itemDtoList.sort(Comparator.comparing(ItemDto::getId));
        assertEquals(2, itemDtoList.size());
        assertEquals(1L, itemDtoList.get(0).getId());
        assertEquals("Предмет 1", itemDtoList.get(0).getName());
        assertEquals(1, itemDtoList.get(0).getComments().size());
        assertEquals("Комментарий 1", itemDtoList.get(0).getComments().get(0).getText());
        assertEquals(2L, itemDtoList.get(1).getId());
        assertEquals("Предмет 2", itemDtoList.get(1).getName());
    }
}
