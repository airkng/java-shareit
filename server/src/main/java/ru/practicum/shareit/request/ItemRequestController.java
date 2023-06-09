package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping()
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                     @RequestBody final ItemRequestCreationDto itemRequestCreationDto) {
        return service.add(userId, itemRequestCreationDto);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllMyRequests(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                 @RequestParam(defaultValue = "0") final int from,
                                                 @RequestParam(defaultValue = "10") final int size) {

        return service.getAllMyRequest(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                               @RequestParam(defaultValue = "0") final int from,
                                               @RequestParam(defaultValue = "10") final int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                         @PathVariable final Long requestId) {
        return service.getById(userId, requestId);
    }
}
