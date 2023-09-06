package ru.practicum.shareit.exceptions;

public class ItemExistException extends RuntimeException {
    public ItemExistException(String message) {
        super(message);
    }
}
