package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepositoryImpl extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorId(Long userId);

    List<Comment> findAllByItemOwnerId(Long userId);

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIn(List<Item> itemList);
}
