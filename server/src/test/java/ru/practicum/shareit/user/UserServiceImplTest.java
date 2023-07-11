package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepositoryDb;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    private final UserServiceImpl userService;
    private final UserRepositoryDb userRepository;
    private final ItemRepositoryDb itemRepository;

    private UserCreationDto userCreationDto = UserCreationDto.builder()
            .name("test")
            .email("test@email.com")
            .build();

    private UserCreationDto u2 = UserCreationDto.builder()
            .name("test2")
            .email("test2@email.com")
            .build();

    private UserCreationDto u3 = UserCreationDto.builder()
            .name("test3")
            .email("test3@email.com")
            .build();

    UserDto dto;
    UserDto dto2;
    UserDto dto3;

    @BeforeEach
    public void prepareDb() {
        dto = userService.create(userCreationDto);
        dto2 = userService.create(u2);
        dto3 = userService.create(u3);
    }

    @Test
    void addUser_CorrectValues_ShouldReturnDto() {
        UserDto answer = userService.create(UserCreationDto.builder()
                .email("test4@email.com")
                .name("test4")
                .build());

        assertNotNull(answer);
        assertNotNull(answer.getId());
        assertEquals("test4", answer.getName());
        assertEquals("test4@email.com", answer.getEmail());

        Optional<User> retrievedUser = userRepository.findById(answer.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("test4", retrievedUser.get().getName());
        assertEquals("test4@email.com", retrievedUser.get().getEmail());
    }

    @Test
    void addUser_AlreadyExistEmail_ShouldReturnException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.create(userCreationDto);
        });
    }

    @Test
    void getUsers_ShouldReturnList() {
        List<UserDto> users = userService.getAll();
        assertEquals(3, users.size());
        assertEquals("test", users.get(0).getName());
        assertEquals("test@email.com", users.get(0).getEmail());
    }

    @Test
    void getUserById_CorrectValues_ShouldReturnDto() {
        UserDto retrievedUser = userService.get(dto2.getId());
        assertNotNull(retrievedUser);
        assertEquals(dto2.getId(), retrievedUser.getId());
        assertEquals(dto2.getName(), retrievedUser.getName());
        assertEquals(dto2.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void getUserById_invalidUser_ShouldReturnNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.get(100L));
    }

    @Test
    void updateUser_invalidUser_shouldReturnNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.update(u2, -1L));
    }

    @Test
    void updateUser_correctData_ShouldReturnDto() {

        userCreationDto.setName("change");
        userCreationDto.setEmail("updated@email.com");
        UserDto updatedUser = userService.update(userCreationDto, 1L);
        assertNotNull(updatedUser);
        assertEquals(1L, updatedUser.getId());
        assertEquals("change", updatedUser.getName());
        assertEquals("updated@email.com", updatedUser.getEmail());

        Optional<User> retrievedUser = userRepository.findById(updatedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("change", retrievedUser.get().getName());
        assertEquals("updated@email.com", retrievedUser.get().getEmail());
    }

    @Test
    void deleteUser() {
        userService.delete(dto3.getId());

        Optional<User> deletedUser = userRepository.findById(dto3.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void containsUser_ExistUser_ShouldReturnTrue() {
        assertTrue(userService.contains(dto.getId()));
        assertTrue(userService.contains(dto2.getId()));
        assertTrue(userService.contains(dto3.getId()));
    }

    @Test
    void containsUser_NotExistUser_ShouldReturnFalse() {
        assertFalse(userService.contains(-1L));
    }

}
