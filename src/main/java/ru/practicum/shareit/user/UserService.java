package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto get(Long userId);

    List<UserDto> getAll();

    UserDto create(UserCreationDto userCreationDto);

    UserDto update(UserCreationDto user, Long userId);

    void delete(Long userId);

    boolean contains(Long userId);
}
