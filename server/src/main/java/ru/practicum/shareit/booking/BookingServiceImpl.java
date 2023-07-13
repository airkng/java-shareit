package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StateNotSupportException;
import ru.practicum.shareit.item.ItemRepositoryDb;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryDb;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingMapper mapper;
    private final BookingRepository bookingRepository;
    private final ItemRepositoryDb itemRepository;
    private final UserRepositoryDb userRepository;

    @Override
    public BookingDto create(final BookingCreationDto bookingCreationDto, final Long bookerId) {
        Booking booking = mapper.toBooking(bookingCreationDto);
        checkDateTime(booking.getStart(), booking.getEnd());
        Item item = itemRepository.findById(bookingCreationDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException(String.format("Item with id = %d is not found", bookingCreationDto.getItemId()));
        });
        User owner = item.getOwner();
        if (owner.getId().equals(bookerId)) {
            //по-хорошему стоит бы выбрасывать 400 http, но тестировщики решили 404
            throw new NotFoundException("You cant booking by yourself");
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with id = %d is not found", bookerId));
        });
        if (!item.getAvailable()) {
            throw new NotAvailableException("Item is not available");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    private static void checkDateTime(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Incorrect date");
        }
    }

    @Override
    public BookingDto updateStatus(final Long bookingId, final Long userId, final Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
                    throw new NotFoundException(String.format("Booking with id = %d not found", bookingId));
                });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("You haven't access to this action");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException("You've already approved");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto get(final Long bookingId, final Long userId) {
        if (userRepository.existsById(userId)) {
            try {
                return mapper.toBookingDto(bookingRepository.findById(bookingId).filter((booking) -> {
                    if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
                        return true;
                    } else {
                        throw new NotFoundException("Invalid user with id");
                    }
                }).orElseThrow(() -> {
                    throw new NotFoundException("Booking with id = %d is not found");
                }));
            } catch (NullPointerException e) {
                throw new NotFoundException(String.format("Booking with id = %d is not found", bookingId));
            }
        } else {
            throw new NotFoundException(String.format("User with id = %d is not found", userId));
        }
    }

    @Override
    public List<BookingDto> getAll(final Long userId, final String state, final Boolean isOwner, final Integer from, final Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found " + userId);
        }
        Page<Booking> bookings = null; //TODO: возможно убрать инициализацию
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pages = PageRequest.of(from == 0 ? 0 : from / size, size, sort);
        LocalDateTime now = LocalDateTime.now();
        if (isOwner) {
            switch (convertToEnum(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByItemOwnerId(userId, pages);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, pages);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, pages);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, pages);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pages);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pages);
                    break;
            }
        } else {
            switch (convertToEnum(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pages);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, now, now, pages);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, pages);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, now, pages);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pages);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pages);
                break;
            }
        }
        return bookings.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    private static State convertToEnum(final String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new StateNotSupportException("Unknown state: " + state);
        }
    }
}
