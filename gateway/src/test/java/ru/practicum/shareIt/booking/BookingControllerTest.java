package ru.practicum.shareIt.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    private static LocalDateTime start;
    private static LocalDateTime end;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void prepareTestClass() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
    }

    @Test
    public void addBooking_InvalidDtoNotCorrectStartDate_ShouldReturnBadRequest() throws Exception {
        BookItemRequestDto badDto = BookItemRequestDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().minusDays(1))
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(badDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBooking_InvalidDtoNotCorrectEndDate_ShouldReturnBadRequest() throws Exception {
        BookItemRequestDto badDto = BookItemRequestDto.builder()
                .itemId(2L)
                .start(start)
                .end(LocalDateTime.now().minusHours(3))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(badDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllBookingByState_InvalidPageFrom_ShouldReturnBadRequestStatus() throws Exception {
        String state = "WAITING";
        int from = -1;
        int size = 10;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllBookingByState_InvalidPageSize_Should() throws Exception {
        String state = "WAITING";
        int from = 0;
        int size = -1;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state).param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllItemsBookings_InvalidPageFrom_ShouldReturnBadRequest() throws Exception {
        String state = "ALL";
        int from = -1;
        int size = 0;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllItemsBookings_InvalidPageSizeAndFrom_ShouldReturnBadRequest() throws Exception {
        Long invalidUserId = 1L;
        String state = "ALL";
        int from = -7;
        int size = -1;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", invalidUserId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBooking_InvalidDtoNoItemId_ShouldReturnBadRequest() throws Exception {
        BookItemRequestDto badDto = BookItemRequestDto.builder()
                .start(start)
                .end(end.minusDays(10))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(badDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
