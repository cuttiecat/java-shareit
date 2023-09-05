package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto addItem(ItemDto itemDto, Long owner);

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long owner);

    public void deleteItem(Long itemId, Long owner);

    public ItemDto getItem(Long itemId, Long userId);

    public List<ItemDto> getItemsByOwner(Long owner, Integer from, Integer size);

    public List<ItemDto> getItemsByName(String name, Integer from, Integer size);

    public CommentDto addCommentToItem(Long itemId, CommentDto commentDto, Long authorId);
}
