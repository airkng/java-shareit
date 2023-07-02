
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StateNotSupportException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;



    UserCreationDto userDto1 = UserCreationDto.builder().email("user1@email.com").name("user1").build();
    UserCreationDto userDto2 = UserCreationDto.builder().email("user2@email.com").name("user2").build();
    User user1 = User.builder().email("user1@email.com").name("user1").id(1L).build();
    User user2 = User.builder().email("user2@email.com").name("user2").id(2L).build();

    ItemCreationDto itemDto1 = ItemCreationDto.builder().userId(1L).name("item1").description("desc1").available(true).build();
    ItemCreationDto itemDto2 = ItemCreationDto.builder().userId(2L).name("item2").description("desc2").available(true).build();
    ItemCreationDto itemDto3 = ItemCreationDto.builder().userId(2L).name("item2").description("desc2").available(false).build();

    Item item1 = Item.builder().owner(user1).available(true).description("desc1").name("item1").id(1L).build();
    Item item2 = Item.builder().owner(user2).available(true).description("desc2").name("item2").id(2L).build();

    Booking booking1;
    Booking booking2;
    Booking booking3;

    @BeforeEach
    public void beforeEach() {
        userService.create(userDto1);
        userService.create(userDto2);

        itemService.create(itemDto1);
        itemService.create(itemDto2);
        itemService.create(itemDto3);

        booking1 = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());
        booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(BookingStatus.WAITING).build());

        booking3 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING).build());

    }

    @Test
    void addBooking_CorrectData_shouldReturnDto() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(item1.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .build();


        var actual = bookingService.create(bookingEntryDto, 2L);
        assertEquals(bookingEntryDto.getStart(), actual.getStart());
        assertEquals(bookingEntryDto.getEnd(), actual.getEnd());
        assertEquals(user2, actual.getBooker());
        assertEquals(BookingStatus.WAITING, actual.getStatus());

        List<BookingDto> testBookings1 = bookingService.getAll(1L, "WAITING", true, 0, 10);
        System.out.println("RESULT!!!!!!!!!!!!!");
        System.out.println("RESULT!!!!!!!!!!!!!");
        System.out.println(testBookings1);
        assertEquals(2, testBookings1.size());
        assertEquals(4L, testBookings1.get(0).getId());
        assertEquals(1L, testBookings1.get(1).getId());

        assertTrue(testBookings1.get(0).getStart().isAfter(testBookings1.get(1).getStart()));

    }

    @Test
    void addBooking_InvalidUserId_shouldReturnNotFound() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(item1.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingEntryDto, -1L));
    }

    @Test
    void addBooking_InvalidItemId_shouldReturnNotFound() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(-1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingEntryDto, user1.getId()));
    }

    @Test
    void addBooking_OwnItem_shouldReturnNotFound() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(item1.getId())
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingEntryDto, user1.getId()));
    }

    @Test
    void addBooking_UnavailableItem_shouldReturnNotAvailableException() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(3L).start(LocalDateTime.now().minusHours(1)).end(LocalDateTime.now()).build();

        assertThrows(NotAvailableException.class, () -> bookingService.create(bookingEntryDto, user1.getId()));
    }

    @Test
    void addBooking_InvalidDate_ShouldReturnIllegalArgumentException() {
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder().itemId(1L).start(LocalDateTime.now().minusHours(1)).end(LocalDateTime.now().minusHours(2)).build();

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingEntryDto, user1.getId()));
    }

    @Test
    void approveBooking() {
        bookingService.updateStatus(1L, 1L, true);

        List<BookingDto> testBookings = bookingService.getAll(1L, "CURRENT", true, 0, 10);
        assertEquals(1, testBookings.size());

        List<BookingDto> testBookingStatusCurrent = bookingService.getAll(2L, "CURRENT", false, 0, 10);
        assertEquals(1, testBookingStatusCurrent.size());
        assertEquals(1L, testBookingStatusCurrent.get(0).getId());
    }

    @Test
    void disApproveBooking() {
        bookingService.updateStatus(1L, 1L, false);
        List<BookingDto> testBookingStatusRejected = bookingService.getAll(1L, "REJECTED", true, 0, 10);
        assertEquals(1, testBookingStatusRejected.size());
        assertEquals(1L, testBookingStatusRejected.get(0).getId());
    }

    @Test
    void approveBooking_NotOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(2L, 1L, true));
    }

    @Test
    void approveBooking_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(1L, -100L, true));
    }

    @Test
    void approveBooking_InvalidBooking() {
        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(-1L, 1L, true));
    }

    @Test
    void approveBooking_Duplicate() {
        bookingService.updateStatus(1L, 1L, true);
        assertThrows(NotAvailableException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void getBookingById() {
        BookingDto bookingDto = bookingService.get(1L, 1L);
        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingDto.getItem().getId());
        assertEquals(user2, bookingDto.getBooker());
        assertEquals(user1, bookingDto.getItem().getOwner());
        assertEquals(item1, bookingDto.getItem());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertTrue(LocalDateTime.now().isBefore(bookingDto.getEnd()));
        assertTrue(LocalDateTime.now().isAfter(bookingDto.getStart()));
    }

    @Test
    void getBookingById_InvalidItem() {
        assertThrows(NotFoundException.class, () -> bookingService.get(99L, 1L));
    }

    @Test
    void getBookingById_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 111L));
    }

    @Test
    void getBookingById_InvalidAccessByUser_ShouldReturnNotFound() {

    }

    @Test
    void getAllBookingByStateWaiting() {
        List<BookingDto> testBookings = bookingService.getAll(2L, "WAITING", true, 0, 10);
        assertEquals(2, testBookings.size());
        bookingService.updateStatus(2L, 2L, false);

        List<BookingDto> onlyOne = bookingService.getAll(2L, "REJECTED", true, 0, 10);
        System.out.println(onlyOne);
        assertEquals(1, onlyOne.size());
        assertEquals(2L, onlyOne.get(0).getId());
        assertEquals(item2, onlyOne.get(0).getItem());
    }

    @Test
    void getAllBooking_InvalidUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getAll(-99L, "WAITING", false, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAll(-99L, "WAITING", true, 0, 10));
    }

    @Test
    void getAllBooking_InvalidState() {
        assertThrows(StateNotSupportException.class, () -> bookingService.getAll(1L, "GAY", true, 0, 10));
        assertThrows(StateNotSupportException.class, () -> bookingService.getAll(1L, "NotGAY", false, 0, 10));
    }

    @Test
    void getAllBookingByStateRejected()  {
        bookingService.updateStatus(1L, 1L, false);
        List<BookingDto> testBookingStatusRejected = bookingService.getAll(2L, "REJECTED", false, 0, 10);
        assertEquals(1, testBookingStatusRejected.size());
        var bookingsRejected = bookingService.getAll(1L, "REJECTED", true, 0, 10);
        assertEquals(1, bookingsRejected.size());
    }

    @Test
    void getAllBookingByStateCurrent()  {
        bookingService.updateStatus(1L, 1L, true);

        List<BookingDto> testBookingStatusCurrent = bookingService.getAll(2L, "CURRENT", false,0, 10);
        assertEquals(1, testBookingStatusCurrent.size());
        assertEquals(1L, testBookingStatusCurrent.get(0).getId());
    }

    @Test
    void getAllBookingByStateAll()  {
        List<BookingDto> testBookingStatusAll = bookingService.getAll(2L, "ALL", true, 0, 10);
        assertEquals(testBookingStatusAll.size(), 2);
        assertEquals(bookingService.getAll(1L, "ALL", false, 0, 10).size(), 2);
    }


    @Test
    void getAllBookingByStatePast()  {
        bookingService.updateStatus(2L, 2L, true);

        List<BookingDto> testBookingStatusPast = bookingService.getAll(2L, "PAST", true, 0, 10);
        assertEquals(1, testBookingStatusPast.size());
        assertEquals(2L, testBookingStatusPast.get(0).getId());

        var booker = bookingService.getAll(1L, "PAST", false, 0, 10);
        assertEquals(1, testBookingStatusPast.size());
        assertEquals(2L, testBookingStatusPast.get(0).getId());
    }

    @Test
    void getAllBookingByStateFuture() {
        bookingService.updateStatus(3L, 2L, true);
        List<BookingDto> testBookingStatusFuture = bookingService.getAll(2L, "FUTURE", true, 0, 10);
        assertEquals(1, testBookingStatusFuture.size());
        assertEquals(3L, testBookingStatusFuture.get(0).getId());

        List<BookingDto> bookerList = bookingService.getAll(1L, "FUTURE", false, 0, 10);
        assertEquals(1, testBookingStatusFuture.size());
        assertEquals(3L, testBookingStatusFuture.get(0).getId());
    }
}

