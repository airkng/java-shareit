package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item get(Integer itemId, Integer userId);

    Item create(Item item);

    Item update(Item item);

    List<Item> getAll(Integer userId);

    List<Item> search(String text);
}
