package ru.practicum.shareit.exceptions;

public class ItemWithWrongOwner extends RuntimeException {
    public ItemWithWrongOwner(String message) {
        super(message);
    }
}
