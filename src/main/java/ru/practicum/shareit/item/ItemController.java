package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return mapper.toItemDto(itemService.get(itemId, userId));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAll(userId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchText(@RequestParam(value = "text") String text) {
        return itemService.search(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemCreationDto itemCreationDto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        itemCreationDto.setUserId(userId);
        Item item = mapper.toItem(itemCreationDto);
        return mapper.toItemDto(itemService.create(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto replaceItem(@PathVariable("itemId") Integer itemId,
                               @RequestBody ItemCreationDto itemCreationDto,
                               @RequestHeader("X-Sharer-User-Id") Integer userId) {
        itemCreationDto.setUserId(userId);
        Item item = mapper.toItem(itemCreationDto);
        item.setId(itemId);
        return mapper.toItemDto(itemService.update(item));
    }

}
