package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable(value = "userId") final Integer userId) {
        return mapper.toUserDto(userService.get(userId));
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid final UserCreationDto userDto) {
        User user = mapper.toUser(userDto);
        return mapper.toUserDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public UserDto replaceUser(@PathVariable("userId") final Integer userId,
                               @RequestBody final UserCreationDto userDto) {
        userDto.setId(userId);
        User user = mapper.toUser(userDto);
        return mapper.toUserDto(userService.replace(user, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") final Integer userId) {
        userService.delete(userId);
    }
}
