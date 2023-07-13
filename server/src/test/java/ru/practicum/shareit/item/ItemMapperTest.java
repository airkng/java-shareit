package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {
    private final ItemMapper mapper = new ItemMapper();

    @Test
    void toItemDto() {
        Item item = Item.builder().id(1L).name("Test Item").owner(User.builder().build()).description("Test item description").available(true).build();
        ItemDto itemDto = mapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner(), itemDto.getOwner());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItem() {
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(1L).name("Test Item").description("Test item description").available(true).requestId(1L).build();
        Item item = mapper.toItem(itemDto);

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getUserId(), item.getOwner().getId());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest().getRequestId());
    }

    @Test
    void toItemForRequest() {
        Item item = Item.builder().id(1L).owner(User.builder().id(1L).build()).name("Test Item").description("Test item description").request(ItemRequest.builder().requestId(3L).build()).available(true).build();
        ItemForRequest itemForRequest = mapper.toItemForRequest(item);

        assertEquals(item.getId(), itemForRequest.getId());
        assertEquals(item.getName(), itemForRequest.getName());
        assertEquals(item.getDescription(), itemForRequest.getDescription());
        assertEquals(item.getAvailable(), itemForRequest.getAvailable());
        assertEquals(item.getRequest().getRequestId(), itemForRequest.getRequestId());
        assertNotNull(itemForRequest.getRequestId());
    }
}
