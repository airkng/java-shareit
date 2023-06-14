package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final HashMap<Integer, Item> items = new HashMap<>();

    public Optional<Item> get(final Integer itemId, final Integer userId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Item create(final Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Item update(final Item item) {
        items.replace(item.getId(), item);
        return item;
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
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
