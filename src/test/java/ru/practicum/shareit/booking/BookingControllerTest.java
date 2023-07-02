package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    private static BookingCreationDto bookingDto;
    private static BookingDto bookingResponse;

    private static LocalDateTime start;
    private static LocalDateTime end;
    private static Long userId = 1L;
    private static Long invalidUserId = -100L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    public static void prepareTestClass() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        bookingDto = BookingCreationDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        bookingResponse = BookingDto
                .builder()
                .id(1L)
                .item(Item.builder()
                        .id(1L)
                        .build())
                .booker(User.builder().id(1L).build()).status(BookingStatus.WAITING)
                .start(start)
                .end(end)
                .build();
    }

    @Test
    public void createBooking_correctValues_shouldReturnSameObject() throws Exception {
        when(bookingService.create(ArgumentMatchers.any(BookingCreationDto.class), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .content(objectMapper.writeValueAsString(bookingDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", equalTo(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$.start", equalTo(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void testAddBooking_InvalidUserId_ShouldReturnNotFoundStatus() throws Exception {
        when(bookingService.create(ArgumentMatchers.any(BookingCreationDto.class), anyLong()))
                .thenThrow(new NotFoundException("User not found."));
        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", invalidUserId)
                .content(objectMapper.writeValueAsString(bookingDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddBooking_InvalidDtoNoItemId_ShouldReturnBadRequest() throws Exception {
        BookingCreationDto badDto = BookingCreationDto.builder()
                .start(start)
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .content(objectMapper.writeValueAsString(badDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddBooking_InvalidDtoNotCorrectStartDate_ShouldReturnBadRequest() throws Exception {
        BookingCreationDto badDto = BookingCreationDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().minusDays(1))
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .content(objectMapper.writeValueAsString(badDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddBooking_InvalidDtoNotCorrectEndDate_ShouldReturnBadRequest() throws Exception {
        BookingCreationDto badDto = BookingCreationDto.builder()
                .itemId(2L)
                .start(start)
                .end(LocalDateTime.now().minusHours(3))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(badDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testApproveBooking_CorrectData_ShouldReturnOkAndResponseStatusApproved() throws Exception {
        Long bookingId = 1L;
        BookingDto response = bookingResponse;
        response.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(response);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId)
                .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", equalTo(bookingResponse.getStatus().toString())));

    }

    @Test
    public void testApproveBooking_InvalidUserId_ShouldReturnNotFoundStatus() throws Exception {
        Long bookingId = 1L;
        Boolean approved = true;
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("User not found."));
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", invalidUserId)
                .param("approved", String.valueOf(approved)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetBookingById_CorrectValue_ShouldReturnSameDto() throws Exception {
        Long bookingId = 1L;

        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", equalTo(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$.start", equalTo(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void testGetBookingById_InvalidUserId() throws Exception {
        Long bookingId = 1L;
        when(bookingService.get(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", invalidUserId)).andExpect(status().isNotFound());
    }

    @Test
    public void testGetBookingById_InvalidItemId() throws Exception {
        Long bookingId = 1L;
        when(bookingService.get(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found."));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", invalidUserId)).andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllBookingByState_CorrectRequest_ShouldReturnListAndOkStatus() throws Exception {
        Long userId = 1L;
        String state = "WAITING";
        int from = 0;
        int size = 10;
        BookingDto booking1 = BookingDto.builder()
                .id(2L)
                .item(Item.builder().id(2L).build())
                .booker(User.builder()
                        .id(2L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1)).status(BookingStatus.REJECTED)
                .build();

        List<BookingDto> bookingList = new ArrayList<>();
        Collections.addAll(bookingList, bookingResponse, booking1);

        when(bookingService.getAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].item.id", is(booking1.getItem().getId().intValue())))
                .andExpect(jsonPath("$[1]booker.id", is(booking1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[1]start", equalTo(booking1.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

    }

    @Test
    public void testGetAllBookingByState_InvalidPageFrom_ShouldReturnBadRequestStatus() throws Exception {
        String state = "WAITING";
        int from = -1;
        int size = 10;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", invalidUserId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllBookingByState_InvalidPageSize_Should() throws Exception {
        String state = "WAITING";
        int from = 0;
        int size = -1;

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", invalidUserId)
                .param("state", state).param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllBookingByState_InvalidUserId_shouldReturnNoFoundStatus() throws Exception {
        String state = "WAITING";
        int from = 0;
        int size = 10;

        when(bookingService.getAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", invalidUserId)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllItemsBookings_CorrectValues_ShouldReturnListAndOkStatus() throws Exception {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        BookingDto booking2 = BookingDto.builder()
                .id(2L)
                .item(Item.builder().id(2L).build())
                .booker(User.builder().id(2L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();

        List<BookingDto> bookingList = Arrays.asList(bookingResponse, booking2);

        when(bookingService.getAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", userId)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].item.id", is(booking2.getItem().getId().intValue())))
                .andExpect(jsonPath("$[1].booker.id", is(booking2.getBooker().getId().intValue())));
    }

    @Test
    public void testGetAllItemsBookings_InvalidPageFrom_ShouldReturnBadRequest() throws Exception {
        String state = "ALL";
        int from = -1;
        int size = 0;

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", invalidUserId)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllItemsBookings_InvalidPageSizeAndFrom_ShouldReturnBadRequest() throws Exception {
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
}