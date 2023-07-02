package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class ItemServiceImplTest {

    private final ItemService itemService;

    private final ItemRequestService itemRequestService;

    private final BookingService bookingService;

    private final UserService userService;

    private ItemMapper mapper = new ItemMapper();

    private final LocalDateTime now = (LocalDateTime.now());

    User user1 = User.builder()
            .id(1L)
            .name("test1")
            .email("test1@email.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("test2")
            .email("test2@email.ru")
            .build();

    UserCreationDto userDto1 = UserCreationDto.builder()
            .name("test1")
            .email("test1@email.ru")
            .build();

    UserCreationDto userDto2 = UserCreationDto.builder()
            .name("test2")
            .email("test2@email.ru")
            .build();


    Item item1 = Item.builder()
            .id(1L)
            .owner(user1)
            .description("test1")
            .name("test1")
            .available(true)
            .build();

    Item item2 = Item.builder()
            .id(2L)
            .owner(user2)
            .description("test2")
            .name("test2")
            .available(true)
            .build();

    ItemCreationDto itemDto1 = ItemCreationDto.builder()
            .userId(user1.getId())
            .available(true)
            .description("test1")
            .name("test1")
            .build();

    ItemCreationDto itemDto2 = ItemCreationDto.builder()
            .userId(user2.getId())
            .available(true)
            .description("test2")
            .name("test2")
            .build();

    CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("comment1")
            .authorName("test2")
            .itemId(item2.getId())
            .created(now)
            .build();

    BookingCreationDto bookingDto1 = BookingCreationDto.builder()
            .itemId(item1.getId())
            .start(now.minusDays(1))
            .end(now.minusHours(10))
            .build();

    @BeforeEach
    public void beforeEach() {
        userService.create(userDto1);
        userService.create(userDto2);

        itemService.create(itemDto1);
        itemService.create(itemDto2);

        bookingService.create(bookingDto1, user2.getId());
        bookingService.updateStatus(1L, user1.getId(), true);
    }

    @Test
    void getAll_correctInfo_shouldReturnListAndOk() {
        List<ItemDto> itemsTest = itemService.getAll(user1.getId(), 0, 10);

        assertNotNull(itemsTest);
        assertEquals(1, itemsTest.size());
        assertEquals(item1.getDescription(), itemsTest.get(0).getDescription());
        assertEquals(item1.getName(), itemsTest.get(0).getName());
        assertEquals(item1.getOwner(), itemsTest.get(0).getOwner());
        assertEquals(item1.getId(), itemsTest.get(0).getId());

        List<ItemDto> items2Test = itemService.getAll(user2.getId(), 0, 10);

        assertNotNull(items2Test);
        assertEquals(1, items2Test.size());
        assertEquals(item2.getDescription(), items2Test.get(0).getDescription());
        assertEquals(item2.getName(), items2Test.get(0).getName());
        assertEquals(item2.getOwner(), items2Test.get(0).getOwner());


    }

    @Test
    void getItemById_CorrectValues_ShouldReturnDto() {
        ItemDto itemDto = itemService.get(1L, 1L);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals(item1.getName(), itemDto.getName());
        assertEquals(item1.getDescription(), itemDto.getDescription());
        assertEquals(item1.getOwner(), itemDto.getOwner());
        assertEquals(2L, itemDto.getLastBooking().getBookerId());

        ItemDto itemDto1 = itemService.get(2L, 1L);
        assertNotNull(itemDto1);
        assertEquals(item2.getId(), itemDto1.getId());
        assertEquals(item2.getName(), itemDto1.getName());
        assertEquals(item2.getDescription(), itemDto1.getDescription());
        assertEquals(item2.getOwner(), itemDto1.getOwner());
        assertNull(itemDto1.getLastBooking());
    }

    @Test
    void getItemById_invalidItem_shouldReturnNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.get(-2L, 1L));
    }

    @Test
    void updateItem_CorrectValues_shouldReturnUpdatedDto() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(user1.getId())
                .name("Updated").description("Updated").available(false).build();
        itemService.update(itemDto, 1L);

        ItemDto testItem = itemService.get(1L, 1L);
        assertEquals(1L, testItem.getId());
        assertEquals("Updated", testItem.getName());
        assertEquals("Updated", testItem.getDescription());
        assertEquals(1L, testItem.getOwner().getId());
    }

    @Test
    void updateItem_invalidItemId_shouldReturnUserAccess() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(user1.getId())
                .name("Updated").description("Updated").available(false).build();
        assertThrows(UserAccessException.class, () -> itemService.update(itemDto, 2L));
    }

    @Test
    void updateItem_invalidItemId_shouldReturnNotFound() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(user1.getId())
                .name("Updated").description("Updated").available(false).build();
        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, -1L));
    }

    @Test
    void updateItem_invalidUser_shouldReturnNotFoundException() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(999L)
                .name("Updated").description("Updated").available(false).build();
        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 1L));
    }

    @Test
    void updateItem_invalidUser_shouldReturnUserAccessException() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(user2.getId())
                .name("Updated").description("Updated").available(false).build();
        assertThrows(UserAccessException.class, () -> itemService.update(itemDto, 1L));
    }

    @Test
    void searchItem_shouldReturnCorrectValues() throws ValidationException {
        ItemDto item11 = itemService.create(ItemCreationDto.builder().name("Дрель").description("Дрель эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item22 = itemService.create(ItemCreationDto.builder().name("Дрель ручная").description("Дрель ручная").userId(user2.getId())
                .available(true).build());
        ItemDto item33 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item44 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка ручная").userId(user2.getId())
                .available(false).build());

        Collection<ItemDto> items = itemService.search("Дрель", 0, 10);
        assertThat(items).hasSize(2);
        assertThat(items).contains(item11, item22);

        Collection<ItemDto> items1 = itemService.search("Эллектрическая", 0, 10);
        assertThat(items1).hasSize(2);
        assertThat(items1).contains(item11, item33);

        Collection<ItemDto> items2 = itemService.search("ручная", 0, 10);
        assertThat(items2).hasSize(1);
        assertThat(items2).contains(item22);
    }

    @Test
    void searchItem_BlankSearch_shouldReturnEmptyList() throws ValidationException {
        ItemDto item11 = itemService.create(ItemCreationDto.builder().name("Дрель").description("Дрель эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item22 = itemService.create(ItemCreationDto.builder().name("Дрель ручная").description("Дрель ручная").userId(user2.getId())
                .available(true).build());
        ItemDto item33 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item44 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка ручная").userId(user2.getId())
                .available(false).build());

        Collection<ItemDto> items = itemService.search(" ", 0, 10);
        assertThat(items).hasSize(0);
    }

    @Test
    void searchItem_NoSuchItem_shouldReturnEmptyList() throws ValidationException {
        ItemDto item11 = itemService.create(ItemCreationDto.builder().name("Дрель").description("Дрель эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item22 = itemService.create(ItemCreationDto.builder().name("Дрель ручная").description("Дрель ручная").userId(user2.getId())
                .available(true).build());
        ItemDto item33 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка эллектрическая").userId(user1.getId())
                .available(true).build());
        ItemDto item44 = itemService.create(ItemCreationDto.builder().name("Отвертка").description("Отвертка ручная").userId(user2.getId())
                .available(false).build());

        Collection<ItemDto> items = itemService.search("Cтул", 0, 10);
        assertThat(items).hasSize(0);
    }

   @Test
    void addItem() {
        ItemCreationDto testItem = ItemCreationDto.builder().userId(-99L)
                .name("Updated").description("Updated").available(true).build();
        assertThrows(NotFoundException.class, () -> itemService.create(testItem));

        testItem.setUserId(user1.getId());
        var correctDto = itemService.create(testItem);

        assertEquals(testItem.getUserId(), correctDto.getOwner().getId());
        assertEquals(testItem.getName(), correctDto.getName());
        assertEquals(testItem.getDescription(), correctDto.getDescription());
        assertEquals(testItem.getAvailable(), correctDto.getAvailable());

    }

    @Test
    void addItem_invalidUser_shouldReturnNotFoundException() {
        ItemCreationDto testItem = ItemCreationDto.builder().userId(-1L)
                .name("Updated").description("Updated").available(true).build();
        assertThrows(NotFoundException.class, () -> itemService.create(testItem));
    }

    @Test
    void addItem_invalidRequest_shouldReturnNotFound() {
        ItemCreationDto testItem = ItemCreationDto.builder().userId(user1.getId())
                .name("Updated").description("Updated").available(true).requestId(99L).build();

        assertThrows(NotFoundException.class, () -> itemService.create(testItem));
    }

    @Test
    void addComment_shouldReturnCorrectDto() {
        var bookings = bookingService.get(1L, user2.getId());
        System.out.println(bookings);
        /*ItemDto itemDto = itemService.getItems(2L).get(0);
        assertEquals(1, itemDto.getComments().size());*/
        var res = itemService.addComment(user2.getId(), item2.getId(), commentDto1);
        System.out.println(res);
        assertEquals(res.getText(), commentDto1.getText());
        assertEquals(res.getAuthorName(), commentDto1.getAuthorName());
        assertEquals(res.getItemId(), commentDto1.getItemId());
    }

    @Test
    void addComment_shouldReturnNotAvailableException() {
        var bookings = bookingService.get(1L, user2.getId());
        System.out.println(bookings);
        BookingCreationDto bookingDto2 = BookingCreationDto.builder()
                .itemId(item2.getId())
                .start(now.minusDays(1))
                .end(now.plusDays(10))
                .build();
        bookingService.create(bookingDto2, user1.getId());
        bookingService.updateStatus(2L, user2.getId(), true);


        assertThrows(NotAvailableException.class, () -> {
            itemService.addComment(user1.getId(), item2.getId(), commentDto1);
        });
    }

   @Test
    void addComment_invalidUser() {
        assertThrows(NoSuchElementException.class, () -> itemService.addComment(99L, 2L, commentDto1));
    }

    @Test
    void addComment_invalidItem() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 99L, commentDto1));
    }
}
