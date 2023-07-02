package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private UserCreationDto userCreationDto = UserCreationDto.builder()
            .id(1L)
            .name("test")
            .email("test@email.com")
            .build();
    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("test")
            .email("test@email.com")
            .build();

    @Test
    void getUsers_ShouldReturnOkAndEmptyList() throws Exception {
        Mockito.when(userService.getAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserById_invalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;
        Mockito.when(userService.get(invalidId))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_CorrectId_ShouldReturnOkAnd() throws Exception {
        Mockito.when(userService.get(1L))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", equalTo(userDto.getEmail())))
                .andExpect(jsonPath("$.name", equalTo(userDto.getName())));
    }

    @Test
    void addUser_CorrectValues_ShouldReturnOkAndBody() throws Exception {

        Mockito.when(userService.create(Mockito.any(UserCreationDto.class)))
                .thenReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser_CorrectData_ShouldReturnOkAndBody() throws Exception {

        Mockito.when(userService.update(Mockito.any(UserCreationDto.class), anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser_invalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;
        Mockito.when(userService.update(Mockito.any(UserCreationDto.class), anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + invalidId)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteUser_CorrectId_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.DELETE, "/users/" + 1));

        Mockito.verify(userService, Mockito.times(1))
                .delete(userCreationDto.getId());
    }
}
