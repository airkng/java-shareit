package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item get(Integer itemId, Integer userId) {
        return itemRepository.get(itemId, userId).orElseThrow(() -> {
            throw new ItemNotFoundException(String.format("Item with user id = %d and item id = %d not found", userId, itemId));
        });
    }

    @Override
    public Item create(Item item) {
        if (userService.contains(item.getOwner().getId())) {
            return itemRepository.create(item);
        } else {
            throw new UserNotFoundException(String.format("User with user id = %d not found", item.getOwner().getId()));
        }
    }

    @Override
    public Item update(Item item) {
        if (userService.contains(item.getOwner().getId())) {
            return itemRepository.update(item);
        } else {
            throw new UserNotFoundException(String.format("User with user id = %d not found", item.getOwner().getId()));
        }
    }

    @Override
    public List<Item> getAll(Integer userId) {
        Optional<List<Item>> items = itemRepository.getAll();
        return items.map(itemList -> itemList.stream()
                        .filter(item -> item.getOwner().getId().equals(userId))
                        .collect(Collectors.toList())
                )
                .orElseGet(List::of);

    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text);
    }
}
