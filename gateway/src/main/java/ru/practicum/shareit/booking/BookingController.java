package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.HEADER_USER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(HEADER_USER) Long userId,
										     @RequestBody @Valid BookingDto bookingDto) {

		log.info("Пользователь {}, добавить новое бронирование", userId);
		return bookingClient.addBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(HEADER_USER) Long userId,
												@PathVariable Long bookingId,
												@RequestParam Boolean approved) {

		log.info("Пользователь {}, изменил статус бронирования {}", userId, bookingId);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER_USER) Long userId,
											 	 @PathVariable Long bookingId) {

		log.info("Забронировать {}", bookingId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByBookerId(@RequestHeader(HEADER_USER) Long userId,
														   @RequestParam(name = "state", defaultValue = "all") String stateParam,
														   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
														   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));

		log.info("Получить список бронирования по идентификатору бронирования {}", userId);
		return bookingClient.getAllBookingsByBookerId(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsForAllItemsByOwnerId(@RequestHeader(HEADER_USER) Long userId,
														   			 @RequestParam(name = "state", defaultValue = "all") String stateParam,
														   			 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
														   			 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));

		log.info("Получить все бронирования для всех товаров по идентификатору владельца {}.", userId);
		return bookingClient.getAllBookingsForAllItemsByOwnerId(userId, state, from, size);
	}
}
