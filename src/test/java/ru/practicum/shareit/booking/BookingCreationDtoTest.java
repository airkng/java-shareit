package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingCreationDtoTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


    @Test
    public void serializeToJson() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        BookingCreationDto bookingEntryDto = BookingCreationDto.builder()
                .start(LocalDateTime.of(2020, 1, 1, 12, 3))
                .end(LocalDateTime.of(2020, 1, 1, 12, 10))
                .itemId(3L)
                .build();

        String json = objectMapper.writeValueAsString(bookingEntryDto);
        String expectedJson = "{\"itemId\":3,\"start\":\"2020-01-01T12:03:00\",\"end\":\"2020-01-01T12:10:00\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    public void deserializeFromJson() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String json = "{\"start\":\"2023-05-01T10:00:00\",\"end\":\"2023-05-01T12:00:00\",\"itemId\":2}";

        BookingCreationDto dto = objectMapper.readValue(json, BookingCreationDto.class);

        BookingCreationDto expectedDto = BookingCreationDto.builder()
                .start(LocalDateTime.of(2023, 5, 1, 10, 0))
                .end(LocalDateTime.of(2023, 5, 1, 12, 0))
                .itemId(2L)
                .build();

        assertEquals(expectedDto, dto);
    }
}
