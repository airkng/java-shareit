package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemForRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ItemRequestCreationDto itemRequestCreationDto = ItemRequestCreationDto.builder()
            .description("test")
            .build();

    private ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("test")
            .build();

    @Test
    void addRequestTest_correctValues_shouldReturnOk() throws Exception {
        when(service.add(anyLong(), any(ItemRequestCreationDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestCreationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(itemRequestDto.getDescription())));
    }

    @Test
    void addRequestTest_InvalidDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestCreationDto invalidDto = ItemRequestCreationDto.builder().build();

        when(service.add(anyLong(), any(ItemRequestCreationDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        ItemRequestCreationDto emptyDto = ItemRequestCreationDto.builder()
                .description("")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(emptyDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRequest_InvalidUserId_shouldReturnNotFound() throws Exception {
        Long invalidId = -1L;
        when(service.add(anyLong(), any(ItemRequestCreationDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", invalidId)
                        .content(mapper.writeValueAsString(itemRequestCreationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOwnRequests_correctValues_shouldReturnList() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemForRequest request1 = new ItemForRequest();
        request1.setId(7L);
        request1.setDescription("Item desc");

        ItemForRequest request2 = new ItemForRequest();
        request2.setId(8L);
        request2.setDescription("item desc2");

        List<ItemForRequest> ownRequests = Arrays.asList(request1, request2);
        itemRequestDto.setItems(ownRequests);

        Mockito.when(service.getAllMyRequest(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description")
                        .value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items.[0].id")
                        .value(request1.getId()))
                .andExpect(jsonPath("$[0].items.[0].description").value(request1.getDescription()));
    }

    @Test
    public void getOwnRequests_invalidPageFrom_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = -1;
        int size = 10;

        Mockito.when(service.getAllMyRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

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

        Mockito.when(service.getAllMyRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

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

        Mockito.when(service.getAllMyRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getOwnRequests_InvalidUser_shouldReturnNotFound() throws Exception {
        int from = 0;
        int size = 10;

        when(service.getAllMyRequest(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("User not found!"));


        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 0L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRequests_CorrectValues_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemForRequest request1 = new ItemForRequest();
        request1.setId(7L);
        request1.setDescription("Item desc");

        ItemForRequest request2 = new ItemForRequest();
        request2.setId(8L);
        request2.setDescription("item desc2");

        List<ItemForRequest> ownRequests = Arrays.asList(request1, request2);
        itemRequestDto.setItems(ownRequests);

        Mockito.when(service.getAll(eq(userId), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description")
                        .value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items.[0].id")
                        .value(request1.getId()))
                .andExpect(jsonPath("$[0].items.[0].description").value(request1.getDescription()));
    }

    @Test
    public void testGetAllRequests_InvalidPageFrom_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        int from = -1;
        int size = 10;

        Mockito.when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

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

        Mockito.when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllRequests_InvalidUser_shouldReturnNotFound() throws Exception {
        int from = 0;
        int size = 10;

        when(service.getAll(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("User not found!"));


        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 0L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestById_correctValues_shouldReturnDtoAndOk() throws Exception {
        Long userId = 1L;
        Long requestId = 2L;

        ItemForRequest items = new ItemForRequest();
        items.setId(requestId);
        items.setDescription("item");

        itemRequestDto.setItems(List.of(items));

        when(service.getById(eq(userId), eq(requestId)))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.items.[0].id").value(items.getId()))
                .andExpect(jsonPath("$.items.[0].description").value(items.getDescription()));
    }
}
