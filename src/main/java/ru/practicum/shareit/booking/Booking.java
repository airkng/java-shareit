package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private User user;
    private Item item;

    private LocalDateTime start;
    private LocalDateTime end;

    private Boolean status;

}
