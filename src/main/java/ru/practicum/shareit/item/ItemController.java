package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable final Long itemId,
                           @RequestHeader("X-Sharer-User-Id") final Long userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") final Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchText(@RequestParam(value = "text") final String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid final ItemCreationDto itemCreationDto,
                              @RequestHeader("X-Sharer-User-Id") final Long userId) {
        itemCreationDto.setUserId(userId);
        return itemService.create(itemCreationDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") final Long itemId,
                              @RequestBody final ItemCreationDto itemCreationDto,
                              @RequestHeader("X-Sharer-User-Id") final Long userId) {
        itemCreationDto.setUserId(userId);
        return itemService.update(itemCreationDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                 @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(id, itemId, commentDto);
    }

}
