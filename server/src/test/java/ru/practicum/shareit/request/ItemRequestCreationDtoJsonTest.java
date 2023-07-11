package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;

public class ItemRequestCreationDtoJsonTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializeToJsonTest() throws JsonProcessingException {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ItemRequestCreationDto dto = ItemRequestCreationDto.builder()
                .description("test")
                .build();
        String expected = mapper.writeValueAsString(dto);
        String actual = "{\"description\":\"test\"}";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deserializationToJson() throws JsonProcessingException {
        String json = "{\"description\":\"test\"}";
        var expected = mapper.readValue(json, ItemRequestCreationDto.class);
        ItemRequestCreationDto dto = ItemRequestCreationDto.builder()
                .description("test")
                .build();
        Assertions.assertEquals(expected, dto);
    }
}
