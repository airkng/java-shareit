package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoJsonTest {

    ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    //.setDateFormat(new StdDateFormat().withColonInTimeZone(true))


    @Test
    public void testSerializeToJson() throws JsonProcessingException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(dateFormat);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test description")
                .created(Timestamp.valueOf(LocalDateTime.of(2023, 5, 1, 10, 0))).build();

        String expectedJson = "{\"id\":1,\"description\":\"Test description\",\"created\":\"2023-05-01T10:00:00\"," +
                "\"items\":null}";
        String actualJson = mapper.writeValueAsString(itemRequestDto);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testDeserializeFromJson() throws JsonProcessingException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(dateFormat);
        String json = "{\"id\":1,\"description\":\"Test description\"," +
                "\"created\":\"2023-05-01T10:00:00\" }";

        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test description")
                .created(Timestamp.valueOf(LocalDateTime.of(2023, 5, 1, 10, 0)))
                .build();

        ItemRequestDto actualItemRequestDto = mapper.readValue(json, ItemRequestDto.class);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }
}