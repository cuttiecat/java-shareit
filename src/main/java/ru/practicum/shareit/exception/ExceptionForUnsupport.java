package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
public class ExceptionForUnsupport extends RuntimeException {
    public ExceptionForUnsupport(String message) {
        super(message);
    }
}