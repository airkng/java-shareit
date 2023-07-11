package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient client;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable(value = "userId") final Long userId) {
        return client.get(userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        return client.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid final UserDto userDto) {
        return client.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") final Long userId,
                              @RequestBody final UserDto userDto) {
        userDto.setId(userId);
        return client.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") final Long userId) {
        client.delete(userId);
    }

}
