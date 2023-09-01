package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.EntryNotFoundException;
import ru.practicum.shareit.error.InvalidRequestParamsException;
import ru.practicum.shareit.error.ItemNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntryNotFoundException(
                    String.format("Бронирование с указанным id (%d) не существует", bookingId)
            );
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, @Valid BookingInDto booking) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(booking.getItemId());
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(
                    String.format("Вещь с id = %d не доступна для бронирования", item.getId())
            );
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new EntryNotFoundException(
                    String.format("Вещь с id = %d не найдена", item.getId())
            );
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new InvalidRequestParamsException("Дата окончания бронирования должна быть после даты начала");
        }
        Booking newBooking = new Booking();
        newBooking.setStartDate(booking.getStart());
        newBooking.setEndDate(booking.getEnd());
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Override
    @Transactional
    public BookingDto setBookingApproveStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntryNotFoundException(
                    String.format("Бронирование с указанным id (%d) не существует", bookingId)
            );
        }
        if (approved && booking.getStatus() == BookingStatus.APPROVED) {
            throw new ItemNotAvailableException(
                    String.format("Вещь с id = %d не доступна для бронирования", booking.getItem().getId())
            );
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateFilter) {
        getUserOrThrow(userId);
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        switch (stateFilter) {
            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndDateIsBefore(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllCurrentForBooker(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartDateIsAfter(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId, BookingStatus.WAITING, sort
                );
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId, BookingStatus.REJECTED, sort
                );
                break;
            case "ALL":
                bookings = bookingRepository.findByBookerId(
                        userId, sort
                );
                break;
            default:
                throw new InvalidRequestParamsException(
                        String.format("Unknown state: %s", stateFilter)
                );
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String stateFilter) {
        getUserOrThrow(userId);
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        switch (stateFilter) {
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndDateIsBefore(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllCurrentForOwner(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartDateIsAfter(
                        userId, LocalDateTime.now(), sort
                );
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId, BookingStatus.WAITING, sort
                );
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED, sort
                );
                break;
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(userId, sort);
                break;
            default:
                throw new InvalidRequestParamsException(
                        String.format("Unknown state: %s", stateFilter)
                );
        }
        return BookingMapper.toBookingDto(bookings);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntryNotFoundException(
                                String.format("Пользователь с указанным id (%d) не существует", userId)
                        )
                );
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntryNotFoundException(
                                String.format("Вещь с id = %d не найдена", itemId)
                        )
                );
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntryNotFoundException(
                                String.format("Бронирование с указанным id (%d) не существует", bookingId)
                        )
                );
    }
}
