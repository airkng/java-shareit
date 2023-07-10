package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public BookingDto addBooking(@Valid @RequestBody final BookingCreationDto bookingCreationDto,
                                 @RequestHeader("X-Sharer-User-Id") final Long userId) {
        return bookingService.create(bookingCreationDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingApproving(@PathVariable final Long bookingId,
                                       @RequestHeader("X-Sharer-User-Id") final Long userId,
                                       @RequestParam(name = "approved") final Boolean approved) {
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable final Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") final Long userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllForBooker(@RequestParam(name = "state", defaultValue = "ALL") final String state,
                                            @RequestHeader("X-Sharer-User-Id") final long bookerId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                            @RequestParam(defaultValue = "10") @PositiveOrZero final int size) {
        final Boolean isOwner = false;
        return bookingService.getAll(bookerId, state, isOwner, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestParam(name = "state", defaultValue = "ALL") final String state,
                                           @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                           @RequestParam(defaultValue = "10") @PositiveOrZero final int size) {
        final Boolean isOwner = true;
        return bookingService.getAll(ownerId, state, isOwner, from, size);
    }

}
