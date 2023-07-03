package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceMockTests {
    private final UserService service;

    @MockBean
    private final UserRepositoryDb repository;

    private User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();

    private UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();

    private UserCreationDto userCreationDto1 = UserCreationDto.builder()
            .name("user1")
            .email("user1@email.com")
            .build();

    @Test
    void createUser_CorrectData_shouldReturnDto() {
        when(repository.save(any(User.class)))
                .thenReturn(user1);
        UserDto dto = service.create(userCreationDto1);

        assertThat(dto, equalTo(userDto1));
    }

    @Test
    void getUserById_correctData_shouldReturnSameDto() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        var dto = service.get(1L);

        assertThat(dto, equalTo(userDto1));
    }

    @Test
    void getUserById_incorrectUser_throwsException() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(-1L), "User with -1 user id not found");
    }

    @Test
    void updateUser_changeName_shouldReturnUpdatedResult() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        User change = User.builder()
                .id(1L)
                .name("change1")
                .email("user1@email.com")
                .build();

        when(repository.save(any(User.class)))
                .thenReturn(change);

        UserDto user = service.update(userCreationDto1, 1L);

        assertThat(change.getId(), equalTo(user.getId()));
        assertThat(change.getName(), equalTo(user.getName()));
        assertThat(change.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser_changeEmail_shouldReturnUpdatedResult() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        User change = User.builder()
                .id(1L)
                .name("user1")
                .email("change1@email.com")
                .build();

        when(repository.save(any(User.class)))
                .thenReturn(change);

        UserDto user = service.update(userCreationDto1, 1L);

        assertThat(change.getId(), equalTo(user.getId()));
        assertThat(change.getName(), equalTo(user.getName()));
        assertThat(change.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser_incorrectUser_throwsException() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(userCreationDto1, -1L), "User with -1 user id not found");
    }

    @Test
    void containsUser_ExistUser_shouldReturnTrue() {
        when(repository.existsById(any(Long.class)))
                .thenReturn(true);

        assertEquals(service.contains(10L), true);
    }

    @Test
    void containsUser_NonExistUser_shouldReturnFalse() {
        when(repository.existsById(anyLong()))
                .thenReturn(false);

        assertEquals(service.contains(1L), false);
    }

    @Test
    void deleteUser_AnyUser_shouldUse1Times() {
        service.delete(1L);
        verify(repository, Mockito.times(1)).deleteById(anyLong());
    }
}
