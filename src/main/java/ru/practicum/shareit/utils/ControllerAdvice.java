package ru.practicum.shareit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

import javax.validation.ValidationException;

@RestControllerAdvice
@Slf4j
@Validated
public class ControllerAdvice {
    public static final String ERROR_400 = "Ошибка 400";
    public static final String ERROR_400_DESCRIPTION = "Ошибка валидации";

    public static final String ERROR_404 = "Ошибка 404";
    public static final String ERROR_404_DESCRIPTION = "Искомый объект не найден";

    public static final String ERROR_500 = "Unknown state: UNSUPPORTED_STATUS";
    public static final String ERROR_500_DESCRIPTION = "Возникло исключение";

    @ExceptionHandler({ItemNotAvailableException.class, MethodArgumentNotValidException.class,
            ValidationException.class, BookingChangeStatusException.class, CommentBookerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse fourHundredErrorHandle(final Exception exception) {
        log.warn(ERROR_400, exception);
        return new ErrorResponse(ERROR_400, ERROR_400_DESCRIPTION);
    }

    @ExceptionHandler({ItemWithWrongOwner.class, ItemWithoutOwnerException.class, UserExistException.class,
            ItemExistException.class, BookingExistException.class, RequestExistException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse fourHundredFourErrorHandle(Exception exception) {
        log.warn(ERROR_404, exception);
        return new ErrorResponse(ERROR_404, ERROR_404_DESCRIPTION);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse fiveHundredErrorHandle(final Throwable exception) {
        log.warn(ERROR_500, exception);
        return new ErrorResponse(ERROR_500, ERROR_500_DESCRIPTION);
    }
}