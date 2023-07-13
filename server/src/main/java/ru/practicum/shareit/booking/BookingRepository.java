package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemId(Long itemId, Sort sort);

    Page<Booking> findAllByBookerId(Long bookerId, Pageable page);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable page);

    Boolean existsByBookerIdAndEndBeforeAndStatus(Long bookerId, LocalDateTime localDateTime, BookingStatus status);


    Page<Booking> findAllByItemOwnerId(Long ownerId, Pageable sort);

    Page<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable page);
}
