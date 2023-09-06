package ru.practicum.shareit.booking.bookingUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoBooking;
import ru.practicum.shareit.user.model.User;

@Slf4j
@UtilityClass
public class BookingMapper {
    public Booking toBookingFromReceivedDto(ReceivedBookingDto bookingDto, Item item, User user) {
        log.info("Начата процедура преобразования ДТО в бронирование: {}", bookingDto);
        return new Booking(null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING);
    }

    public ReturnBookingDto toBookingDto(Booking booking) {
        log.info("Начата процедура преобразования брони в ДТО: {}", booking);
        return new ReturnBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserDtoBooking(booking.getBooker().getId(), booking.getBooker().getName()),
                new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName()));
    }
}
