package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;
    private static final String CONTROLLER_LOG = "Контроллер запросов получил запрос на {}{}";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ReturnRequestDto addRequest(@Valid @RequestBody ReceivedRequestDto requestDto,
                                       @RequestHeader(USER_HEADER) Long requestorId) {
        log.info(CONTROLLER_LOG, "Добавление запроса: ", requestDto);
        return requestService.addRequest(requestDto, requestorId);
    }

    @GetMapping("/all")
    public List<ReturnRequestDto> getOthersRequests(@PositiveOrZero @RequestParam(value = "from",
                                                            required = false) Integer from,
                                                    @Positive @RequestParam(value = "size",
                                                            required = false) Integer size,
                                                    @RequestHeader(USER_HEADER) Long userId) {
        log.info(CONTROLLER_LOG, "Получение запросов постранично начиная с: ", from);
        return requestService.getOthersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ReturnRequestDto getRequest(@PathVariable Long requestId,
                                       @RequestHeader(USER_HEADER) Long userId) {
        log.info(CONTROLLER_LOG, "Получение запроса с id: ", requestId);
        return requestService.getRequest(requestId, userId);
    }

    @GetMapping
    public List<ReturnRequestDto> getUserRequests(@RequestHeader(USER_HEADER) Long requestorId) {
        log.info(CONTROLLER_LOG, "Получение всех запросов пользователя: ", requestorId);
        return requestService.getUserRequests(requestorId);
    }
}
