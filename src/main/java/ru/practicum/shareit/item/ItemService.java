package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto get(Long itemId, Long userId);

    ItemDto create(ItemCreationDto item);

    ItemDto update(ItemCreationDto item, Long itemId);

    List<ItemDto> getAll(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
