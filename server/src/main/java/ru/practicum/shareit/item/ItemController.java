package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.util.Constant.HEADER_USER;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(HEADER_USER) Long userId,
                                           @RequestBody ItemDto itemDto) {

        log.info("Пользователь {}, добавил новый предмет {}", userId, itemDto.getName());
        return ResponseEntity.ok(itemService.addItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_USER) Long userId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {

        log.info("Пользователь {}, обновил предмет {}", userId, itemDto.getName());
        return ResponseEntity.ok(itemService.updateItem(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(HEADER_USER) Long userId,
                                           @PathVariable Long itemId) {
        log.info("Получен предмет {}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsUser(@RequestHeader(HEADER_USER) Long userId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Список предметов пользователя {}", userId);
        return ResponseEntity.ok(itemService.getItemsUser(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItem(@RequestParam String text,
                                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Получить элемент с ключевой подстрокой {}", text);
        return ResponseEntity.ok(itemService.searchItem(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(HEADER_USER) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody CommentDto commentDto) {

        log.info("Пользователь {} добавил комментарий к предмету {}", userId, itemId);
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }
}