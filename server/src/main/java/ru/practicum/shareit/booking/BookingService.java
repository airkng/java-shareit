package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingCreationDto bookingCreationDto, Long userId);

    BookingDto updateStatus(Long bookingId, Long userId, Boolean approved);

    BookingDto get(Long bookingId, Long userId);

    List<BookingDto> getAll(Long userId, String state, Boolean isOwner, Integer from, Integer size);
}
