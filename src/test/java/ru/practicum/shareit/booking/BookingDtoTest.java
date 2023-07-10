package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoTest {
    private ObjectMapper mapper = new ObjectMapper();
    private final LocalDateTime start = LocalDateTime.of(2020, 1, 1, 12, 3);
    private final LocalDateTime end = LocalDateTime.of(2020, 1, 1, 12, 10);

    @Test
    void jsonSerialization() throws JsonProcessingException {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        BookingDto dto = BookingDto.builder()
                .id(123L)
                .booker(User.builder()
                        .id(1L)
                        .email("test@email.com")
                        .build())
                .item(Item.builder()
                        .id(99L)
                        .available(true)
                        .description("test")
                        .build())
                .status(BookingStatus.CANCELLED)
                .end(end)
                .start(start)
                .build();
        String json = mapper.writeValueAsString(dto);
        String expected = "{\"id\":123,\"start\":\"2020-01-01T12:03:00\",\"end\":\"2020-01-01T12:10:00\"," +
                "\"status\":\"CANCELLED\",\"item\":{" +
                "\"id\":99,\"owner\":null,\"name\":null,\"description\":\"test\",\"available\":true,\"request\":null}," +
                "\"booker\":{\"id\":1,\"email\":\"test@email.com\",\"name\":null}}";
        System.out.println(json);
        System.out.println("\n" + expected);
        assertEquals(json, expected);
    }

    @Test
    void deserializationJson() throws JsonProcessingException {
        String json = "{\"id\":1,\"start\":\"2020-01-01T12:03:00\",\"end\":\"2020-01-01T12:10:00\"," +
                "\"status\":\"CANCELLED\",\"item\":{" +
                "\"id\":99,\"owner\":null,\"name\":null,\"description\":\"test\",\"available\":true,\"request\":null}," +
                "\"booker\":{\"id\":1,\"email\":\"test@yamail.com\",\"name\":null}}";
        mapper.registerModule(new JavaTimeModule());
        var result = mapper.readValue(json, BookingDto.class);
        BookingDto expected = new BookingDto();
        expected.setStatus(BookingStatus.CANCELLED);
        expected.setId(1L);
        expected.setEnd(end);
        expected.setStart(start);
        expected.setBooker(User.builder()
                .id(1L)
                .email("test@yamail.com")
                .build());
        expected.setItem(Item.builder()
                .id(99L)
                .available(true)
                .description("test")
                .build());

        assertThat(result, Matchers.is(expected));
    }
}
