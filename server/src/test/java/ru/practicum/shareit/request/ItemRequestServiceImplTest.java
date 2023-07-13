package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;

    private final UserServiceImpl userService;

    private final ItemService itemService;

    private UserCreationDto uc1 = UserCreationDto.builder().id(1L).name("user1").email("user1@email.com").build();
    private UserCreationDto uc2 = UserCreationDto.builder().id(2L).name("user2").email("user2@email.com").build();
    private UserCreationDto uc3 = UserCreationDto.builder().id(3L).name("user3").email("user3@email.com").build();

    ItemCreationDto ic1 = ItemCreationDto.builder().available(true).name("item1").description("item1").userId(1L).build();
    ItemCreationDto ic2 = ItemCreationDto.builder().available(true).name("item2").description("item2").userId(2L).build();

    User user1 = User.builder().id(1L).name("user1").email("user1@email.com").build();

    private ItemRequestCreationDto itemRequest1 = new ItemRequestCreationDto();
    private ItemRequestCreationDto itemRequest2 = new ItemRequestCreationDto();

    private final Timestamp now = Timestamp.valueOf(LocalDateTime.now());


    @BeforeEach
    public void beforeEach() {
        userService.create(uc1);
        userService.create(uc2);
        userService.create(uc3);

        itemService.create(ic1);
        itemService.create(ic2);

        itemRequest1 = ItemRequestCreationDto.builder().description("test1").build();
        itemRequest2 = ItemRequestCreationDto.builder().description("test2").build();
    }

    @Test
    void addRequest() {
        ItemRequestDto testItem = itemRequestService.add(user1.getId(), itemRequest1);
        assertEquals(1L, testItem.getId());
        assertNotNull(testItem.getCreated());
        assertTrue(now.before(testItem.getCreated()));
        assertEquals("test1", testItem.getDescription());
    }

    @Test
    void addRequest_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.add(-100L, itemRequest2);
        });
    }

    @Test
    void getOwnRequests() {
        ItemRequestDto testItem = itemRequestService.add(1L, itemRequest1);
        ItemRequestDto testItem2 = itemRequestService.add(2L, itemRequest2);

        List<ItemRequestDto> testOwnRequests = itemRequestService.getAllMyRequest(1L, 0, 10);
        List<ItemRequestDto> testOwnRequests1 = itemRequestService.getAllMyRequest(2L, 0, 10);

        assertEquals(1, testOwnRequests.size());
        assertEquals(1, testOwnRequests1.size());
        assertEquals("test1", testOwnRequests.get(0).getDescription());
        assertEquals("test2", testOwnRequests1.get(0).getDescription());
    }

    @Test
    void getOwnRequests_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllMyRequest(-100L, 0, 10);
        });
    }

    @Test
    void getAll() {
        ItemRequestDto testItem = itemRequestService.add(1L, itemRequest1);
        ItemRequestDto testItem2 = itemRequestService.add(2L, itemRequest2);

        List<ItemRequestDto> testOwnRequests = itemRequestService.getAll(1L, 0, 10);
        List<ItemRequestDto> testOwnRequests1 = itemRequestService.getAll(2L, 0, 10);
        List<ItemRequestDto> testOwnRequests2 = itemRequestService.getAll(3L, 0, 10);

        assertEquals(1, testOwnRequests.size());
        assertEquals(1, testOwnRequests1.size());
        assertEquals(2, testOwnRequests2.size());

        assertEquals("test2", testOwnRequests.get(0).getDescription());
        assertEquals("test1", testOwnRequests1.get(0).getDescription());


    }

    @Test
    void getAll_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAll(100L, 0, 10);
        });
    }

    @Test
    void getRequestById() {
        ItemRequestDto testItem = itemRequestService.add(1L, itemRequest1);
        ItemRequestDto testItem2 = itemRequestService.add(2L, itemRequest2);
        Long firstId = testItem.getId();
        Long secondId = testItem2.getId();

        assertEquals(1L, firstId);
        assertEquals(2L, secondId);

        assertEquals("test1", itemRequestService.getById(1L, firstId).getDescription());
        assertEquals("test2", itemRequestService.getById(2L, secondId).getDescription());

        assertEquals("test1", itemRequestService.getById(2L, firstId).getDescription());
        assertEquals("test2", itemRequestService.getById(1L, secondId).getDescription());

        assertEquals("test1", itemRequestService.getById(3L, firstId).getDescription());
        assertEquals("test2", itemRequestService.getById(3L, secondId).getDescription());
    }

    @Test
    void getRequestById_invalidUser() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getById(-100L, 1L);
        });
    }

    @Test
    void getRequestById_invalidRequest() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getById(uc1.getId(), 100L);
        });
    }
}
