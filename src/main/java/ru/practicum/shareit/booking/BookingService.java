package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingStateFilter;

import javax.validation.Valid;
import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto createBooking(Long userId, @Valid BookingInDto booking);

    BookingDto setBookingApproveStatus(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getUserBookings(Long userId, BookingStateFilter stateFilter);

    List<BookingDto> getOwnerBookings(Long userId, BookingStateFilter stateFilter);
}
