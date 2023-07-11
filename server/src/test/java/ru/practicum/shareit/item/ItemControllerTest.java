package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void getItems() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemDto> expectedResult = new ArrayList<>();

        when(itemService.getAll(userId, 0, 10))
                .thenReturn(expectedResult);
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResult.size())));
    }

    @Test
    public void searchItems() throws Exception {
        String text = "search";
        int from = 0;
        int size = 10;
        List<ItemDto> expectedResult = new ArrayList<>();

        when(itemService.search(text, from, size)).thenReturn(expectedResult);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResult.size())));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemCreationDto itemDto = ItemCreationDto.builder().description("Test").name("Test").requestId(1L).available(true).userId(1L).build();
        ItemDto expectedResult = ItemDto.builder().id(1L).description("Test").name("Test").requestId(1L).available(true).build();

        when(itemService.update(itemDto, itemId)).thenReturn(expectedResult);
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.description", Matchers.is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.name", Matchers.is(expectedResult.getName())))
                .andExpect(jsonPath("$.requestId", Matchers.is(expectedResult.getRequestId().intValue())))
                .andExpect(jsonPath("$.available", Matchers.is(expectedResult.getAvailable())));
    }

    @Test
    public void testUpdateItem_InvalidUser() throws Exception {
        Long userId = 99L;
        Long itemId = 1L;
        ItemCreationDto itemDto = ItemCreationDto.builder().userId(99L).description("Test").name("Test").requestId(1L).available(true).build();

        when(itemService.update(itemDto, itemId)).thenThrow(new NotFoundException("User not found!"));
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItem_InvalidItem() throws Exception {
        Long userId = 1L;
        Long itemId = 99L;
        ItemCreationDto itemDto = ItemCreationDto.builder().description("Test").name("Test").requestId(1L).available(true)
                .build();

        when(itemService.update(any(ItemCreationDto.class), anyLong()))
                .thenThrow(new NotFoundException("Item not found!"));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addItem() throws Exception {
        Long userId = 1L;
        ItemCreationDto itemDto = ItemCreationDto.builder().description("Test").name("Test").requestId(1L).available(true).build();
        ItemDto expectedResult = ItemDto.builder().id(1L).description("Test").name("Test").requestId(1L).available(true).build();

        Mockito.when(itemService.create(any(ItemCreationDto.class)))
                .thenReturn(expectedResult);

        var res = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.description", Matchers.is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.name", Matchers.is(expectedResult.getName())))
                .andExpect(jsonPath("$.requestId", Matchers.is(expectedResult.getRequestId().intValue())))
                .andExpect(jsonPath("$.available", Matchers.is(expectedResult.getAvailable())));
        System.out.println("НИМАНИЕ ТЕСТ");
        System.out.println(res.andReturn().getRequest().getContentAsString());
        System.out.println(res.andReturn().getResponse().getContentAsString());
    }

    @Test
    public void addItem_InvalidUser() throws Exception {
        Long userId = 99L;
        ItemCreationDto itemDto = ItemCreationDto.builder().description("Test").name("Test").requestId(1L).available(true).build();

        when(itemService.create(any(ItemCreationDto.class)))
                .thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(1L).itemId(1L).authorName("Test").text("test").build();
        CommentDto expectedResult = CommentDto.builder().id(1L).itemId(1L).authorName("Test").text("test").build();

        when(itemService.addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentDto.class))).thenReturn(expectedResult);
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.itemId", Matchers.is(expectedResult.getItemId().intValue())))
                .andExpect(jsonPath("$.authorName", Matchers.is(expectedResult.getAuthorName())))
                .andExpect(jsonPath("$.text", Matchers.is(expectedResult.getText())));
    }

    @Test
    public void testAddComment_InvalidUser() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(1L).itemId(1L).authorName("Test").text("test").build();
        when(itemService
                .addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentDto.class)))
                .thenThrow(new NotFoundException("User not found!"));
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getById_shouldReturnOk() throws Exception {
        Mockito.when(itemService.get(anyLong(), anyLong()))
                .thenReturn(ItemDto.builder().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

    }
}
