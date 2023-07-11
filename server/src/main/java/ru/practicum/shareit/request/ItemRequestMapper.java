package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(final ItemRequestCreationDto ircd) {
        return  ItemRequest.builder()
                .description(ircd.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(final ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getRequestId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

}
