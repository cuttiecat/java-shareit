package ru.practicum.shareit.exceptions;

public class BookingExistException extends RuntimeException {
    public BookingExistException(String message) {
        super(message);
    }
}
