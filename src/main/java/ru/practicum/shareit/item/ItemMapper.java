package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Component
public class ItemMapper {
    private static HashMap<Long, ItemRequest> requests = new HashMap<>();

    public ItemDto toItemDto(final Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getRequestId() : null)
                .build();
    }

    public Item toItem(final ItemCreationDto itemCreationDto) {
        return Item.builder()
                .owner(User.builder()
                        .id(itemCreationDto.getUserId())
                        .build())
                .name(itemCreationDto.getName())
                .description(itemCreationDto.getDescription())
                .available(itemCreationDto.getAvailable())
                .request(itemCreationDto.getRequestId() != null ? requests.get(itemCreationDto.getRequestId()) : null)
                .build();
    }

    public ItemForRequest toItemForRequest(Item item) {
        return ItemForRequest.builder()
                .available(item.getAvailable())
                .id(item.getId())
                .requestId(item.getRequest().getRequestId())
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }
}
