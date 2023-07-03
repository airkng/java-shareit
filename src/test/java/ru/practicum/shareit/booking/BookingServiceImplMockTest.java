package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepositoryDb;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryDb;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplMockTest {
    private final BookingService service;
    @MockBean
    private final BookingRepository repository;
    @MockBean
    private final ItemRepositoryDb itemRepository;
    @MockBean
    private final UserRepositoryDb userRepository;

    private final BookingMapper mapper = new BookingMapper();

    private UserCreationDto userDto1 = UserCreationDto.builder().email("user1@email.com").name("user1").build();
    private UserCreationDto userDto2 = UserCreationDto.builder().email("user2@email.com").name("user2").build();
    private User user1 = User.builder().email("user1@email.com").name("user1").id(1L).build();
    private User user2 = User.builder().email("user2@email.com").name("user2").id(2L).build();

    private ItemCreationDto itemDto1 = ItemCreationDto.builder().userId(1L).name("item1").description("desc1").available(true).build();
    private ItemCreationDto itemDto2 = ItemCreationDto.builder().userId(2L).name("item2").description("desc2").available(true).build();
    private Item item1 = Item.builder().owner(user1).available(true).description("desc1").name("item1").id(1L).build();
    private Item item2 = Item.builder().owner(user2).available(true).description("desc2").name("item2").id(2L).build();

    Booking booking1 = Booking.builder()
            .item(item1)
            .booker(user2)
            .status(BookingStatus.WAITING)
            .start(LocalDateTime.now().minusHours(1))
            .end(LocalDateTime.now().plusHours(1))
            .build();

    BookingCreationDto bookingCreationDto1 = BookingCreationDto.builder()
            .itemId(1L)
            .start(booking1.getStart())
            .end(booking1.getEnd())
            .build();

    Booking booking2 = Booking.builder()
            .item(item2)
            .booker(user1)
            .start(LocalDateTime.now().minusHours(2))
            .end(LocalDateTime.now().minusHours(1))
            .status(BookingStatus.WAITING).build();

    Booking booking3 = Booking.builder()
            .item(item2)
            .booker(user1)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .status(BookingStatus.WAITING).build();

    @Test
    void createBooking_correctData_shouldReturnDto() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(booking1);

        var actual = service.create(bookingCreationDto1, 2L);
        assertEquals(mapper.toBookingDto(booking1), actual);
    }

    @Test
    void createBooking_bookingByYourself_shouldReturnNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> service.create(bookingCreationDto1, 1L));
    }

    @Test
    void createBooking_IncorrectItemId_shouldReturnNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> service.create(bookingCreationDto1, 1L));
    }

    @Test
    void createBooking_IncorrectDateTime_shouldReturnIllegalArgumentException() {
        var input = BookingCreationDto.builder()
                .itemId(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .build();
        input.setStart(LocalDateTime.now().plusDays(20));

        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(IllegalArgumentException.class, () -> service.create(input, 1L));

        var input2 = bookingCreationDto1;
        input2.setStart(bookingCreationDto1.getStart().plusHours(2));
        assertThrows(IllegalArgumentException.class, () -> service.create(input2, 1L));
    }

    @Test
    void createBooking_notAvailableItem_shouldReturnNotAvailableException() {
        var outputItem = Item.builder().owner(user1).available(true).description("desc1").name("item1").id(1L).build();
        outputItem.setAvailable(false);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(outputItem));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotAvailableException.class, () -> service.create(bookingCreationDto1, 2L));
    }

    @Test
    void getBooking_correctData_shouldReturnDto() {
        Mockito.when(userRepository.existsById(any(Long.class)))
                .thenReturn(true);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        var actual = service.get(1L, 1L);
        assertEquals(actual, mapper.toBookingDto(booking1));
    }

    @Test
    void getBooking_InCorrectUser_shouldReturnNotFoundException() {
        Mockito.when(userRepository.existsById(any(Long.class)))
                .thenReturn(false);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        assertThrows(NotFoundException.class, () -> service.get(1L, 2L));
    }

    @Test
    void getBooking_NonExistBooking_shouldReturnNotFound() {
        Mockito.when(userRepository.existsById(any(Long.class)))
                .thenReturn(false);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(1L, 2L));
    }

    @Test
    void getBooking_RequestFromInvalidUser_shouldReturnNotFoundException() {
        Mockito.when(userRepository.existsById(any(Long.class)))
                .thenReturn(true);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        assertThrows(NotFoundException.class, () -> service.get(1L, -2L));
    }

    @Test
    void updateStatus_CorrectValue_shouldReturnDto() {
        var input = Booking.builder()
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        input.setStatus(BookingStatus.APPROVED);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(input);

        var actual = service.updateStatus(1L, 1L, true);
        var expected = mapper.toBookingDto(input);
        assertEquals(expected, actual);
    }

    @Test
    void updateStatus_IncorrectBooking_shouldReturnNotFound() {
        var input = Booking.builder()
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(input);

        assertThrows(NotFoundException.class, () -> service.updateStatus(-1L, 1L, true));
    }

    @Test
    void updateStatus_AlreadyApproved_shouldReturnNotAvailableException() {
        var input = Booking.builder()
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();
        input.setStatus(BookingStatus.APPROVED);

        Mockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(input));

        Mockito.when(repository.save(any(Booking.class)))
                .thenReturn(input);

        assertThrows(NotAvailableException.class, () -> service.updateStatus(1L, 1L, true));
    }

}
