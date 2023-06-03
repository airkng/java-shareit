package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static Integer itemIdCount = 1;
    private final ItemRepository itemRepository;
    private final ItemMapper mapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ItemDto get(final Integer itemId, final Integer userId) {
        return mapper.toItemDto(itemRepository.get(itemId, userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Item with user id = %d and item id = %d not found", userId, itemId));
        }));
    }

    @Override
    public List<ItemDto> getAll(final Integer userId) {
        var items = itemRepository.getAll();
        return items.stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(final ItemCreationDto itemCreationDto) {
        Item item = mapper.toItem(itemCreationDto);
        item.setId(itemIdCount);
        userService.get(item.getOwner().getId());
        item.setOwner(userRepository.get(item.getOwner().getId()).get());
        increaseItemId();
        return mapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(final ItemCreationDto itemCreationDto, final Integer itemId) {
        userService.get(itemCreationDto.getUserId());
        Item itemInfo = mapper.toItem(itemCreationDto);
        Optional<Item> optionalOldItem = itemRepository.get(itemId, itemCreationDto.getUserId());
        if (optionalOldItem.isPresent()) {
            Item oldItem = optionalOldItem.get();
            if (!oldItem.getOwner().getId().equals(itemInfo.getOwner().getId())) {
                throw new UserAccessException(String.format("User with user id = %d has not access to update item with item id = %d",
                        itemInfo.getOwner().getId(), itemId));
            }
            if (itemInfo.getAvailable() != null) {
                oldItem.setAvailable(itemInfo.getAvailable());
            }
            if (itemInfo.getName() != null) {
                oldItem.setName(itemInfo.getName());
            }
            if (itemInfo.getDescription() != null) {
                oldItem.setDescription(itemInfo.getDescription());
            }
            return mapper.toItemDto(itemRepository.update(oldItem));
        } else {
            throw new NotFoundException(String.format("Item with item id = %s not found", itemId));
        }
    }

    private static void increaseItemId() {
        itemIdCount++;
    }
}
