package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.bookingUtils.BookingState;
import ru.practicum.shareit.booking.bookingUtils.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserDtoBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private MockMvc mvc;
    private ReceivedBookingDto receivedBookingDto;
    private ReturnBookingDto returnBookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);
        receivedBookingDto = new ReceivedBookingDto(1L, start, end);
        returnBookingDto = new ReturnBookingDto(1L, start, end, BookingStatus.WAITING,
                new UserDtoBooking(1L, "Пользователь 1"), new ItemBookingDto(1L, "Предмет 1"));
    }

    @Test
    void shouldAddBooking() throws Exception {
        when(bookingService.addBooking(any(), anyLong())).thenReturn(returnBookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(receivedBookingDto))
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void shouldSetBookingStatus() throws Exception {
        returnBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.setBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(returnBookingDto);
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void shouldGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(returnBookingDto);
        mvc.perform(get("/bookings/1")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void shouldGetOwnerBookings() throws Exception {
        returnBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.getOwnerBookings(BookingState.ALL, 0, 5, 2L))
                .thenReturn(List.of(returnBookingDto));
        mvc.perform(get("/bookings/owner?from=0&size=5&state={state}", BookingState.ALL)
                        .content("")
                        .header(USER_HEADER, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(BookingStatus.APPROVED
                        .toString())));
    }

    @Test
    void shouldGetBookerBookings() throws Exception {
        returnBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.getBookerBookings(BookingState.CURRENT, 0, 15, 2L))
                .thenReturn(List.of(returnBookingDto));
        mvc.perform(get("/bookings?from=0&size=15&state={state}", BookingState.CURRENT)
                        .content("")
                        .header(USER_HEADER, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(BookingStatus.APPROVED
                        .toString())));
    }
}