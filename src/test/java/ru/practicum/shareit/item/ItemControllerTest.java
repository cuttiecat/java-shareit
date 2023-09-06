package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto receivedItemDto;
    private ItemDto returnItemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        BookingItemDto bookingItemDto = new BookingItemDto(null, null, null);
        receivedItemDto = new ItemDto(null, "Предмет 1", "Описание предмета 1", true,
                List.of(), bookingItemDto, bookingItemDto, 1L);
        returnItemDto = receivedItemDto;
        returnItemDto.setId(1L);
    }

    @Test
    void shouldAddItem() throws Exception {
        when(itemService.addItem(receivedItemDto, 1L)).thenReturn(returnItemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(receivedItemDto))
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Предмет 1")))
                .andExpect(jsonPath("$.description", is("Описание предмета 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1)));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        returnItemDto.setName("Новый предмет 1");
        when(itemService.updateItem(1L, returnItemDto, 1L)).thenReturn(returnItemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(returnItemDto))
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Новый предмет 1")))
                .andExpect(jsonPath("$.description", is("Описание предмета 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1)));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mvc.perform(delete("/items/1")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(returnItemDto);
        mvc.perform(get("/items/1")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Предмет 1")))
                .andExpect(jsonPath("$.description", is("Описание предмета 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1)));
    }

    @Test
    void shouldGetItemsByName() throws Exception {
        when(itemService.getItemsByName("редмет", 0, 2)).thenReturn(List.of(returnItemDto));
        mvc.perform(get("/items/search?from=0&size=2&text={text}", "редмет")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Предмет 1")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Описание предмета 1")))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(true)))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(1)));
    }

    @Test
    void shouldGetItemsByOwner() throws Exception {
        when(itemService.getItemsByOwner(1L, 0, 7)).thenReturn(List.of(returnItemDto));
        mvc.perform(get("/items?from=0&size=7")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Предмет 1")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Описание предмета 1")))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(true)))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(1)));
    }

    @Test
    void shouldAddCommentToItem() throws Exception {
        CommentDto receivedCommentDto =
                new CommentDto(null, "Коммент к предмету 1", "Пользователь 1", LocalDateTime.now());
        receivedCommentDto.setId(1L);
        when(itemService.addCommentToItem(1L, receivedCommentDto, 2L)).thenReturn(receivedCommentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(receivedCommentDto))
                        .header(USER_HEADER, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.text", is("Коммент к предмету 1")));
    }
}