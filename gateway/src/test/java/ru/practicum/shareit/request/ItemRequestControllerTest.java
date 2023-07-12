package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    RequestClient client;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("test")
            .build();

    @Test
    void addRequestTest_InvalidDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestDto invalidDto = ItemRequestDto.builder().build();

        when(client.addRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.accepted().body(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        ItemRequestDto emptyDto = ItemRequestDto.builder()
                .description("")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(emptyDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOwnRequests_invalidPageFrom_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = -1;
        int size = 10;

        Mockito.when(client.getAllOwnRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.accepted().body(List.of(itemRequestDto)));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getOwnRequests_invalidPageSize_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = -10;

        Mockito.when(client.getAllOwnRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.accepted().body(List.of(itemRequestDto)));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getOwnRequests_invalidPageInfo_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = -1;
        int size = -10;

        Mockito.when(client.getAllOwnRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.accepted().body(List.of(itemRequestDto)));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testGetAllRequests_InvalidPageFrom_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = -1;
        int size = 10;

        Mockito.when(client.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.accepted().body(List.of(itemRequestDto)));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllRequests_InvalidPageSize_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = -10;

        Mockito.when(client.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.accepted().body(List.of(itemRequestDto)));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }
}
