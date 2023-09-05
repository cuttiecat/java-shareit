package ru.practicum.shareit.exceptions;

public class BookingChangeStatusException extends RuntimeException {
    public BookingChangeStatusException(String message) {
        super(message);
    }
}
