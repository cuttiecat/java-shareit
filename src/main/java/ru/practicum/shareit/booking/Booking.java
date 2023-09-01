package ru.practicum.shareit.booking;

import lombok.Data;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private final int id;   // уникальный идентификатор бронирования;
    private LocalDateTime start; // дата и время начала бронирования;
    private LocalDateTime end; // дата и время конца бронирования;
    private Item item;  // вещь, которую пользователь бронирует;
    private User booker; // пользователь, который осуществляет бронирование;
    private BookingStatus status; // статус бронирования.
}