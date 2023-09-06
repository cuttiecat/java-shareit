package ru.practicum.shareit.exceptions;

public class IllegalBookingStateException extends IllegalStateException {
    public IllegalBookingStateException(String message) {
        super(message);
    }
}
