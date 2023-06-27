package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable(value = "userId") final Long userId) {
        return userService.get(userId);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid final UserCreationDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") final Long userId,
                              @RequestBody final UserCreationDto userDto) {
        userDto.setId(userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") final Long userId) {
        userService.delete(userId);
    }
}
