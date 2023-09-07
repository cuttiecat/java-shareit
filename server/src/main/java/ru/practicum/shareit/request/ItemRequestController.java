package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.Constant.HEADER_USER;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addRequest(@RequestHeader(HEADER_USER) Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {

        log.info("Пользователь {}, добавил новый запрос", userId);
        return ResponseEntity.ok(itemRequestService.addRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequests(@RequestHeader(HEADER_USER) Long userId) {

        log.info("Получение запросов по идентификатору пользователя {}", userId);
        return ResponseEntity.ok(itemRequestService.getRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(HEADER_USER) Long userId,
                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                               @RequestParam(defaultValue = "10",required = false) Integer size) {

        log.info("Получить запросы всех пользователей ");
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(HEADER_USER) Long userId,
                                                         @PathVariable("requestId") Long requestId) {

        log.info("Получен запрос {}", requestId);
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }
}