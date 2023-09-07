package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotNull(message = "Имя не может быть пустым или содержать пробелы.")
    @NotBlank(message = "Имя не может быть пустым или содержать пробелы.")
    private String name;

    @NotNull(message = "Описание не может быть пустым")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Не может быть пустым")
    private Boolean available;

    @Positive(message = "Должно быть положительным")
    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

}
