package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.exceptions.IllegalBookingStateException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.UserExistException;
import ru.practicum.shareit.utils.ControllerAdvice;
import ru.practicum.shareit.utils.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {
    @Test
    void checkErrorHandler() {
        ControllerAdvice controllerAdvice = new ControllerAdvice();

        ItemNotAvailableException exception400 = new ItemNotAvailableException("Ошибка 400");
        ErrorResponse errorResponse = controllerAdvice.fourHundredErrorHandle(exception400);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Ошибка валидации");

        UserExistException exception404 = new UserExistException("404");
        errorResponse = controllerAdvice.fourHundredFourErrorHandle(exception404);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Искомый объект не найден");

        RuntimeException exception500 = new RuntimeException("Ошибка 500");
        errorResponse = controllerAdvice.fiveHundredErrorHandle(exception500);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Возникло исключение");
    }

    @Test
    void checkBookingStateException() {
        assertThrows(IllegalBookingStateException.class,
                () -> BookingState.getBookingsStateFromString("BookingState.UNSUPPORTED"));
    }
}