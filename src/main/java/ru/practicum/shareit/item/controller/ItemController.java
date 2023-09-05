package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String CONTROLLER_LOG = "Контроллер предметов получил запрос на {}{}";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_HEADER) Long ownerId) {
        log.info(CONTROLLER_LOG, "Добавление предмета: ", itemDto);
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") Long itemId,
                              @RequestBody ItemDto itemDto, @RequestHeader(USER_HEADER) Long ownerId) {
        log.info(CONTROLLER_LOG, "Обновление предмета с id: ", itemId);
        return itemService.updateItem(itemId, itemDto, ownerId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long itemId, @RequestHeader(USER_HEADER) Long ownerId) {
        log.info(CONTROLLER_LOG, "Удаление предмета с id: ", itemId);
        itemService.deleteItem(itemId, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") Long itemId, @RequestHeader(USER_HEADER) Long userId) {
        log.info(CONTROLLER_LOG, "Получение предмета с id: ", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByName(@RequestParam("text") String text,
                                        @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                        @Positive @RequestParam(value = "size", required = false) Integer size) {
        log.info(CONTROLLER_LOG, "Получение всех предметов, содержащих в названии: ", text);
        return itemService.getItemsByName(text, from, size);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                         @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                         @Positive @RequestParam(value = "size", required = false) Integer size) {
        log.info(CONTROLLER_LOG, "Получение всех предметов пользователя с id: ", ownerId);
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_HEADER) Long authorId) {
        log.info(CONTROLLER_LOG, "Добавление комментария предмету с id: ", itemId);
        return itemService.addCommentToItem(itemId, commentDto, authorId);
    }
}
