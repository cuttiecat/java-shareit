package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.bookingUtils.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private ItemRepositoryImpl itemRepository;

    @Autowired
    private BookingRepositoryImpl bookingRepository;

    private static final ShareItPageable PAGEABLE = new ShareItPageable(0, 20, Sort.unsorted());

    @Test
    void shouldFindAllByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository.findAllByBookerId(1L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllCurrentByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllCurrentByBookerId(1L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllPastByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllPastByBookerId(1L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllFutureByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllFutureByBookerId(1L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllWaitingByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING));
        List<Booking> bookingList = bookingRepository.findAllWaitingByBookerId(1L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllRejectedByBookerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.REJECTED));
        List<Booking> bookingList = bookingRepository.findAllRejectedByBookerId(1L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository.findAllByOwnerId(2L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllCurrentByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllCurrentByOwnerId(2L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllPastByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllPastByOwnerId(2L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllFutureByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<Booking> bookingList = bookingRepository
                .findAllFutureByOwnerId(2L, LocalDateTime.now(), PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllWaitingByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING));
        List<Booking> bookingList = bookingRepository.findAllWaitingByOwnerId(2L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindAllRejectedByOwnerId() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.REJECTED));
        List<Booking> bookingList = bookingRepository.findAllRejectedByOwnerId(2L, PAGEABLE).toList();
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals("Предмет №1", bookingList.get(0).getItem().getName());
        assertEquals("Пользователь №1", bookingList.get(0).getBooker().getName());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void shouldFindNearPreviousBookings() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        List<BookingItemDto> bookingList = bookingRepository.findNearPreviousBookings(LocalDateTime.now(), 2L);
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals(1L, bookingList.get(0).getItemId());
        assertEquals(1L, bookingList.get(0).getBookerId());
    }

    @Test
    void shouldFindNearNextBookings() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<BookingItemDto> bookingList = bookingRepository.findNearNextBookings(LocalDateTime.now(), 2L);
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals(1L, bookingList.get(0).getItemId());
        assertEquals(1L, bookingList.get(0).getBookerId());
    }

    @Test
    void shouldFindNearPreviousBooking() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        List<BookingItemDto> bookingList = bookingRepository
                .findNearPreviousBooking(LocalDateTime.now(), 2L, 1L);
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals(1L, bookingList.get(0).getItemId());
        assertEquals(1L, bookingList.get(0).getBookerId());
    }

    @Test
    void shouldFindNearNextBooking() {
        User booker = userRepository.save(new User(1L, "Пользователь №1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь №2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет №1", "Описание предмета №1",
                true, owner, null));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        List<BookingItemDto> bookingList = bookingRepository
                .findNearNextBooking(LocalDateTime.now(), 2L, 1L);
        assertEquals(1, bookingList.size());
        assertEquals(1L, bookingList.get(0).getId());
        assertEquals(1L, bookingList.get(0).getItemId());
        assertEquals(1L, bookingList.get(0).getBookerId());
    }
}