package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient client;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void addItem_InvalidItem() throws Exception {
        Long userId = 99L;
        ItemDto itemDto = ItemDto.builder().description(" ").name(" ").build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchItems_InvalidPageSize() throws Exception {
        String text = "search";
        Integer from = 0;
        Integer size = -1;
        Long userId = 1L;
        List<ItemDto> expectedResult = new ArrayList<>();
        when(client.search(userId, text, from, size))
                .thenReturn(new ResponseEntity<Object>(HttpStatus.OK));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchItems_InvalidPageFrom() throws Exception {
        String text = "search";
        int from = -1;
        int size = 10;
        List<ItemDto> expectedResult = new ArrayList<>();
        when(client.search(1L, text, from, size))
                .thenReturn(new ResponseEntity<Object>(HttpStatus.OK));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addComment_InvalidComment() throws Exception {
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder().id(1L).itemId(1L).authorName("Test").text("").build();
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
