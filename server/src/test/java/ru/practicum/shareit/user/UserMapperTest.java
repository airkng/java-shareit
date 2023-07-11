package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    void toUserDto() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("test")
                .build();
        UserDto userDto = mapper.toUserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void toUser() {
        UserCreationDto userCreationDto = UserCreationDto.builder()
                .id(1L).email("test@example.com")
                .name("test")
                .build();
        User user = mapper.toUser(userCreationDto);
        assertEquals(userCreationDto.getId(), user.getId());
        assertEquals(userCreationDto.getEmail(), user.getEmail());
        assertEquals(userCreationDto.getName(), user.getName());
    }
}
