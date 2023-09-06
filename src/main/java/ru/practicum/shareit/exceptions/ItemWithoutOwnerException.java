package ru.practicum.shareit.exceptions;

import javax.validation.ValidationException;

public class ItemWithoutOwnerException extends ValidationException {
    public ItemWithoutOwnerException(String message) {
        super(message);
    }
}
