package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.bookingUtils.BookingMapper;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.bookingUtils.BookingStatus;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepositoryImpl bookingRepository;
    private final UserRepositoryImpl userRepository;
    private final ItemRepositoryImpl itemRepository;
    private static final String SERVICE_LOG = "Сервис бронирования получил запрос на {}{}";

    public ReturnBookingDto addBooking(ReceivedBookingDto bookingDto, Long userId) {
        log.info(SERVICE_LOG, "Добавление бронирования: ", bookingDto);
        Item item = checkItemExist(bookingDto.getItemId());
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BookingDateValidationException("Ошибка. Даты бронирования не могут содержать null");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Ошибка. Предмет не доступен для бронирования");
        }
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new BookingDateValidationException("Ошибка. Дата начала бронирования должна быть раньше конца");
        }
        if (item.getOwner().getId().longValue() == userId) {
            throw new ItemWithoutOwnerException("Ошибка. Владелец вещи не может направить запрос на ее аренду");
        }
        User user = checkUserExist(userId);
        Booking booking = BookingMapper.toBookingFromReceivedDto(bookingDto, item, user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public ReturnBookingDto setBookingStatus(Long bookingId, Boolean status, Long ownerId) {
        log.info(SERVICE_LOG, "Изменение статуса бронирования на: ", status);
        Booking booking = checkBookingExist(bookingId);
        checkUserIsOwner(ownerId, booking.getItem().getOwner().getId());
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingChangeStatusException("Ошибка. Повторное принятие решения по брони не допускается");
        }
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public ReturnBookingDto getBooking(Long bookingId, Long userId) {
        log.info(SERVICE_LOG, "Получение бронирования с id: ", bookingId);
        checkUserExist(userId);
        Booking booking = checkBookingExist(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserExistException("Ошибка. Запрашивать данные о брони может только причастное к ней лицо");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<ReturnBookingDto> getBookerBookings(BookingState state, Integer from, Integer size, Long bookerId) {
        log.info(SERVICE_LOG, "получение бронирований пользователя с id: ", bookerId);
        checkUserExist(bookerId);
        Page<Booking> bookings;
        Pageable pageable = ShareItPageable.checkPageable(from, size, Sort.unsorted());
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBookerId(bookerId, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByBookerId(bookerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByBookerId(bookerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingByBookerId(bookerId, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedByBookerId(bookerId, pageable);
                break;
            default: bookings = bookingRepository.findAllByBookerId(bookerId, pageable);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReturnBookingDto> getOwnerBookings(BookingState state, Integer from, Integer size, Long ownerId) {
        log.info(SERVICE_LOG, "получение бронирований вещей пользователя с id: ", ownerId);
        checkUserExist(ownerId);
        Page<Booking> bookings;
        Pageable pageable = ShareItPageable.checkPageable(from, size, Sort.unsorted());
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwnerId(ownerId, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByOwnerId(ownerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByOwnerId(ownerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingByOwnerId(ownerId, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedByOwnerId(ownerId, pageable);
                break;
            default: bookings = bookingRepository.findAllByOwnerId(ownerId, pageable);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private User checkUserExist(Long ownerId) {
        log.info("Начата процедура проверки наличия в репозитории пользователя с id: {}", ownerId);
        return userRepository.findById(ownerId).orElseThrow(
                () -> new UserExistException("Ошибка. Запрошенного пользователя в базе данных не существует"));
    }

    private Item checkItemExist(Long itemId) {
        log.info("Начата процедура проверки наличия в репозитории предмета с id: {}", itemId);
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ItemExistException("Ошибка. Запрошенного предмета в базе данных не существует"));
    }

    private void checkUserIsOwner(Long currentOwnerId, Long legalOwnerId) {
        log.info("Начата процедура проверки принадлежности предмета пользователю с id: {}", currentOwnerId);
        if (!(currentOwnerId.longValue() == legalOwnerId.longValue())) {
            throw new ItemWithWrongOwner("Ошибка. Обновить/удалить предмет может только владелец предмета");
        }
    }

    private Booking checkBookingExist(Long bookingId) {
        log.info("Начата процедура проверки наличия в репозитории брони с id: {}", bookingId);
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingExistException("Ошибка. Запрошенной брони в базе данных не существует"));
    }
}
