package ru.practicum.shareit.item.comments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class CommentDtoTest {
    ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true));

    @Test
    void testCommentDtoSerialization() throws JsonProcessingException {
        objectMapper.registerModule(new JavaTimeModule());
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("This is a comment")
                .authorName("John Doe")
                .itemId(2L)
                .created(LocalDateTime.parse("2023-05-29T12:34:56.789"))
                .build();
        String json = objectMapper.writeValueAsString(commentDto);

        String expectedJson = "{\"id\":1,\"text\":\"This is a comment\",\"authorName\":\"John Doe\",\"itemId\":2,\"created\":\"2023-05-29T12:34:56.789\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testCommentDtoDeserialization() throws JsonProcessingException {
        objectMapper.registerModule(new JavaTimeModule());
        String json = "{\"id\":1,\"text\":\"This is a comment\",\"authorName\":\"John Doe\",\"itemId\":2,\"created\":\"2023-05-29T12:34:56.789\"}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        CommentDto expectedCommentDto = CommentDto.builder()
                .id(1L)
                .text("This is a comment")
                .authorName("John Doe")
                .itemId(2L)
                .created(LocalDateTime.parse("2023-05-29T12:34:56.789"))
                .build();

        assertEquals(expectedCommentDto, commentDto);
        //чтобы протестировать аннотацию @Data
        assertEquals(expectedCommentDto.getId(), commentDto.getId());
        assertEquals(expectedCommentDto.getText(), commentDto.getText());
    }
}
