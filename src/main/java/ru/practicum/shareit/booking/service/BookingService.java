package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;

import java.util.List;

public interface BookingService {
    ReturnBookingDto addBooking(ReceivedBookingDto bookingDto, Long userId);

    ReturnBookingDto setBookingStatus(Long bookingId, Boolean status, Long ownerId);

    ReturnBookingDto getBooking(Long bookingId, Long userId);

    List<ReturnBookingDto> getBookerBookings(BookingState state, Integer from, Integer size, Long bookerId);

    List<ReturnBookingDto> getOwnerBookings(BookingState state, Integer from, Integer size, Long ownerId);
}
