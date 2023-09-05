package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

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
public class RequestControllerTest {
    @Mock
    private RequestServiceImpl requestService;
    @InjectMocks
    private RequestController requestController;
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ReceivedRequestDto receivedRequestDto;
    private ReturnRequestDto returnRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        receivedRequestDto = new ReceivedRequestDto("Описание запроса 1");
        returnRequestDto = new ReturnRequestDto(1L, "Описание запроса 1", List.of(), LocalDateTime.now());
    }

    @Test
    void shouldAddRequest() throws Exception {
        when(requestService.addRequest(any(), anyLong())).thenReturn(returnRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(receivedRequestDto))
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Описание запроса 1")))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void shouldGetOthersRequests() throws Exception {
        when(requestService.getOthersRequests(0, 5, 1L)).thenReturn(List.of(returnRequestDto));
        mvc.perform(get("/requests/all?from=0&size=5")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Описание запроса 1")))
                .andExpect(jsonPath("$[*].created", containsInAnyOrder(notNullValue())));
    }

    @Test
    void shouldGetRequest() throws Exception {
        when(requestService.getRequest(1L, 1L)).thenReturn(returnRequestDto);
        mvc.perform(get("/requests/1")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Описание запроса 1")))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        when(requestService.getUserRequests(1L)).thenReturn(List.of(returnRequestDto));
        mvc.perform(get("/requests")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Описание запроса 1")))
                .andExpect(jsonPath("$[*].created", containsInAnyOrder(notNullValue())));
    }
}
