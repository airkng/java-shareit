package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient client;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable final Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") final Long userId) {
        return client.get(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                @RequestParam(defaultValue = "10") @Positive final Integer size) {
        return client.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "text") final String text,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                    @RequestParam(defaultValue = "10") @Positive final Integer size) {
        return client.search(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid final ItemDto itemCreationDto,
                              @RequestHeader("X-Sharer-User-Id") final Long userId) {

        return client.addItem(itemCreationDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") final Long itemId,
                              @RequestBody final ItemDto itemCreationDto,
                              @RequestHeader("X-Sharer-User-Id") final Long userId) {
        return client.update(itemCreationDto, userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                 @PathVariable final Long itemId,
                                 @Valid @RequestBody final CommentDto commentDto) {
        return client.addComment(userId, itemId, commentDto);
    }

}
