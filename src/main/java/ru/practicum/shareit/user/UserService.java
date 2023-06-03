package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto get(Integer userId);

    List<UserDto> getAll();

    UserDto create(UserCreationDto userCreationDto);

    UserDto update(UserCreationDto user, Integer userId);

    void delete(Integer userId);

    boolean contains(Integer userId);
}
