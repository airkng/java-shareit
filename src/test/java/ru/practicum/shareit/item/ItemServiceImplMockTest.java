package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepositoryDb;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplMockTest {
    private final ItemService service;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ItemRepositoryDb itemRepository;
    @MockBean
    private final UserRepositoryDb userRepository;
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    private final BookingMapper mapper = new BookingMapper();

    private User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();

    private Item item1 = Item.builder()
            .id(1L)
            .name("item1")
            .available(true)
            .description("desc1")
            .owner(user1)
            .build();

    private ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("item1")
            .available(true)
            .description("desc1")
            .owner(user1)
            .comments(List.of())
            .build();

    private ItemCreationDto itemCreationDto1 = ItemCreationDto.builder()

            .name("item1")
            .available(true)
            .description("desc1")
            .userId(1L)
            .build();

    private UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();

    @Test
    void getItem_CorrectValue_shouldReturnDto() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        var actual = service.get(1L, 1L);
        assertThat(itemDto1, equalTo(actual));
    }

    @Test
    void getItem_NotCorrectValue_shouldReturnException() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(-1L, -1L));
    }


    @Test
    void setBookingTest_shouldReturnCorrectItemDto() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(Booking.builder().bookingId(2L).status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item1)
                .booker(user1)
                .build());
        when(bookingRepository.findByItemId(1L, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(bookings);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        var dto = service.get(1L, 1L);
        ItemDto newItem = ItemDto.builder()
                .id(1L)
                .name("item1")
                .available(true)
                .description("desc1")
                .owner(user1)
                .comments(List.of())
                .build();
        newItem.setLastBooking(mapper.toItemBookingDto(bookings.get(0)));
        assertEquals(dto, newItem);
    }

    @Test
    void createItem_correctData_shouldReturnDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        var actual = service.create(itemCreationDto1);
        assertEquals(actual.getId(), itemDto1.getId());
        assertEquals(actual.getDescription(), itemDto1.getDescription());
        assertEquals(actual.getName(), itemDto1.getName());
        assertEquals(actual.getOwner(), itemDto1.getOwner());
    }

    @Test
    void createItem_correctDataTestItemRequest_shouldReturnDto() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("req")
                .requestor(user1)
                .build();
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });
        var input = itemCreationDto1;
        input.setRequestId(1L);

        var actual = service.create(itemCreationDto1);
        ItemDto expected = itemDto1;
        expected.setRequestId(1L);
        actual.setId(1L);
        actual.setComments(List.of());

        assertEquals(expected, actual);
    }

    @Test
    void updateItem_setNotAvailable_shouldReturnDto() {
        var itemCreationDto = itemCreationDto1;
        itemCreationDto.setAvailable(false);
        var expected = itemDto1;
        expected.setAvailable(false);
        expected.setComments(null);

        when(userService.get(anyLong()))
                .thenReturn(userDto1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var actual = service.update(itemCreationDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void updateItem_setName_shouldReturnDto() {
        var itemCreationDto = itemCreationDto1;
        itemCreationDto.setName("change");
        var expected = itemDto1;
        expected.setName("change");
        expected.setComments(null);

        when(userService.get(anyLong()))
                .thenReturn(userDto1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var actual = service.update(itemCreationDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void updateItem_setDescription_shouldReturnDto() {
        var itemCreationDto = itemCreationDto1;
        itemCreationDto.setDescription("change");
        var expected = itemDto1;
        expected.setDescription("change");
        expected.setComments(null);

        when(userService.get(anyLong()))
                .thenReturn(userDto1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var actual = service.update(itemCreationDto, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void updateItem_IncorrectItemId_shouldReturnNotFoundException() {
        var itemCreationDto = itemCreationDto1;
        itemCreationDto.setDescription("change");
        var expected = itemDto1;
        expected.setDescription("change");
        expected.setComments(null);

        when(userService.get(anyLong()))
                .thenReturn(userDto1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(NotFoundException.class, () -> service.update(itemCreationDto, 1L));
    }

    @Test
    void updateItem_IncorrectUserAccess_shouldReturnUserAccessException() {
        var itemCreationDto = itemCreationDto1;
        itemCreationDto.setDescription("change");
        itemCreationDto.setUserId(-1L);
        var expected = itemDto1;
        expected.setDescription("change");
        expected.setComments(null);

        when(userService.get(anyLong()))
                .thenReturn(userDto1);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(UserAccessException.class, () -> service.update(itemCreationDto, 1L));
    }


}
