package ru.practicum.shareit.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {
    @JsonProperty("Ошибка")
    private final String message;
}
