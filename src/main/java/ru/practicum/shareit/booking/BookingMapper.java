package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {
    public Booking toBooking(final BookingCreationDto bookingCreationDto) {
        return Booking.builder()
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .build();
    }

    public BookingDto toBookingDto(final Booking booking) {
        return BookingDto.builder()
                .id(booking.getBookingId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .item(booking.getItem())
                .build();
    }
    public BookingItemDto toItemBookingDto(final Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getBookingId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

}
