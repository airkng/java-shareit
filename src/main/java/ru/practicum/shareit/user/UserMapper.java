package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public UserDto toUserDto(final User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

    public User toUser(final UserCreationDto userCreationDto) {
        return User.builder()
                .id(userCreationDto.getId())
                .name(userCreationDto.getName())
                .email(userCreationDto.getEmail())
                .build();
    }
}
