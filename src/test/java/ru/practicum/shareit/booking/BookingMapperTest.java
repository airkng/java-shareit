package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private final BookingMapper mapper = new BookingMapper();

    @Test
    void toBooking() {
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .build();

        Booking booking = mapper.toBooking(bookingCreationDto);

        assertEquals(bookingCreationDto.getStart(), booking.getStart());
        assertEquals(bookingCreationDto.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingDto() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .item(Item.builder()
                        .id(1L)
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .build();
        BookingDto bookingDto = mapper.toBookingDto(booking);

        assertEquals(booking.getBookingId(), bookingDto.getId());
        assertEquals(booking.getItem(), bookingDto.getItem());
        assertEquals(booking.getBooker(), bookingDto.getBooker());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toItemBookingDto() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingItemDto bookingItemDto = mapper.toItemBookingDto(booking);

        assertEquals(booking.getBookingId(), bookingItemDto.getId());
        assertEquals(booking.getItem().getId(), bookingItemDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingItemDto.getBookerId());
        assertEquals(booking.getStart(), bookingItemDto.getStart());
        assertEquals(booking.getEnd(), bookingItemDto.getEnd());
    }
}
