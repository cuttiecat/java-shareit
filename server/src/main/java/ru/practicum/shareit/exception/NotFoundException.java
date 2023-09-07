package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> entityClass, String message) {
        super("Сущность " + entityClass.getSimpleName() + " не найдена. " + message);
    }
}