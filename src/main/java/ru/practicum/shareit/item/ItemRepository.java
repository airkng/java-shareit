package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final UserRepository userRepository;
    private static Integer itemIdCount = 1;
    private final HashMap<Integer, Item> items = new HashMap<>();

    public Optional<Item> get(final Integer itemId, final Integer userId) {
        return Optional.of(items.get(itemId));
    }

    public Item create(final Item item) {
        item.setId(itemIdCount);
        item.setOwner(userRepository.get(item.getOwner().getId()).get());
        items.put(itemIdCount, item);
        itemIdCount++;
        return item;
    }

    public Item update(final Item item) {
        if (items.containsKey(item.getId())) {
            Item oldItem = items.get(item.getId());
            if (!oldItem.getOwner().getId().equals(item.getOwner().getId())) {
                throw new UserAccessException(String.format("User with user id = %d has not access to update item with item id = %d",
                        item.getOwner().getId(), item.getId()));
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            items.replace(oldItem.getId(), oldItem);
            return oldItem;
        } else {
            throw new ItemNotFoundException(String.format("Item with item id = %s not found", item.getId()));
        }
    }

    public Optional<List<Item>> getAll() {
        return Optional.of(new ArrayList<>(items.values()));
    }

    public List<Item> search(final String text) {
        return items.values().stream()
                .filter(
                        item -> item.getAvailable()
                                && (item.getDescription().toLowerCase().contains(text.toLowerCase())
                                || item.getName().toLowerCase().contains(text.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
}
