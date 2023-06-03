package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto get(Integer itemId, Integer userId);

    ItemDto create(ItemCreationDto item);

    ItemDto update(ItemCreationDto item, Integer itemId);

    List<ItemDto> getAll(Integer userId);

    List<ItemDto> search(String text);
}
