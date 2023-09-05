package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String CONTROLLER_LOG = "Контроллер бронирования получил запрос на {}{}";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ReturnBookingDto addBooking(@Valid @RequestBody ReceivedBookingDto bookingDto,
                                       @RequestHeader(USER_HEADER) Long userId) {
        log.info(CONTROLLER_LOG, "добавление брони: ", bookingDto);
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ReturnBookingDto setBookingStatus(@PathVariable Long bookingId, @RequestParam("approved") Boolean status,
                                             @RequestHeader(USER_HEADER) Long ownerId) {
        log.info(CONTROLLER_LOG, "изменение статуса одобрения владельцем брони с id: ", bookingId);
        return bookingService.setBookingStatus(bookingId, status, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ReturnBookingDto getBooking(@PathVariable Long bookingId,
                                       @RequestHeader(USER_HEADER) Long userId) {
        log.info(CONTROLLER_LOG, "получение брони с id: ", bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<ReturnBookingDto> getOwnerBookings(
            @RequestParam(value = "state", required = false) String stateText,
            @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
            @Positive @RequestParam(value = "size", required = false) Integer size,
            @RequestHeader(USER_HEADER) Long ownerId) {
        log.info(CONTROLLER_LOG, "получение брони вещей, принадлежащих пользователем с id: ", ownerId);
        return bookingService.getOwnerBookings(BookingState.getBookingsStateFromString(stateText),
                from, size, ownerId);
    }

    @GetMapping
    public List<ReturnBookingDto> getBookerBookings(
            @RequestParam(value = "state", required = false) String stateText,
            @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
            @Positive @RequestParam(value = "size", required = false) Integer size,
            @RequestHeader(USER_HEADER) Long bookerId) {
        log.info(CONTROLLER_LOG, "получение брони вещей, арендованных пользователю с id: ", bookerId);
        return bookingService.getBookerBookings(BookingState.getBookingsStateFromString(stateText),
                from, size, bookerId);
    }
}