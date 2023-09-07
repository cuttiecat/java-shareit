package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
public class BookingDto {

	private Long itemId;

	@NotNull(message = "Не может быть пустым")
	@FutureOrPresent(message = "Может быть либо в настоящем или будущем")
	private LocalDateTime start;

	@NotNull(message = "Не может быть пустым")
	@Future(message = "Может быть в будущем")
	private LocalDateTime end;

}
