package ru.practicum.shareit.item.itemUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Slf4j
@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        log.info("Начата процедура преобразования комментария в ДТО: {}", comment);
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toComment(CommentDto commentDto, Item item, User author) {
        log.info("Начата процедура преобразования ДТО в комментарий: {}", commentDto);
        return new Comment(commentDto.getId(), commentDto.getText(), item, author, commentDto.getCreated());
    }
}
