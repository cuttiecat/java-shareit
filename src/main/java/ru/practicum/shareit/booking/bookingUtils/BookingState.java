package ru.practicum.shareit.booking.bookingUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exceptions.IllegalBookingStateException;

@Slf4j
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState getBookingsStateFromString(String stateText) {
        log.info("Начата процедура получения типа запроса броней: {}", stateText);
        try {
            if (stateText == null) {
                return BookingState.ALL;
            } else {
                return BookingState.valueOf(stateText);
            }
        } catch (MethodArgumentTypeMismatchException | IllegalArgumentException e) {
            throw new IllegalBookingStateException("Ошибка. в BookingState было передано недопустимое значение");
        }
    }
}
