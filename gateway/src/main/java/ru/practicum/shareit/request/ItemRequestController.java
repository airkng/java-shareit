package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient client;

    @PostMapping()
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return client.addRequest(id, itemRequestDto);
    }

    @Validated
    @GetMapping()
    public ResponseEntity<Object> getAllOwnRequests(@RequestHeader("X-Sharer-User-Id") Long id, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from, @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return client.getAllOwnRequests(id, from, size);
    }

    @Validated
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return client.getAllRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long requestId) {
        return client.getRequestById(id, requestId);
    }

}
