package ru.practicum.shareit.exceptions;

import javax.validation.ValidationException;

public class BookingDateValidationException extends ValidationException {
    public BookingDateValidationException(String message) {
        super(message);
    }
}
