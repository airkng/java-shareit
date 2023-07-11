package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemForRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toItemRequest() {
        ItemRequestCreationDto creationDto = new ItemRequestCreationDto();
        creationDto.setDescription("test");
        var expected = mapper.toItemRequest(creationDto);
        assertEquals(expected.getDescription(), creationDto.getDescription());
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("test");
        dto.setId(1L);
        dto.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        dto.setItems(List.of(ItemForRequest.builder()
                .name("name")
                .id(4L)
                .description("item description")
                .build()));
        ItemRequest request = ItemRequest.builder()
                .requestId(1L)
                .description("test")
                .requestor(User.builder().id(1L).build())
                .build();
        var answer = mapper.toItemRequestDto(request);
        assertEquals(answer.getDescription(), dto.getDescription());
        assertEquals(answer.getId(), dto.getId());
        assertEquals(answer.getCreated().getDate(), dto.getCreated().getDate());
        assertEquals(answer.getCreated().getHours(), dto.getCreated().getHours());
    }
}
