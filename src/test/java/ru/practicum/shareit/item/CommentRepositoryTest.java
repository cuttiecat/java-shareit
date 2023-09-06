package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryImpl;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private ItemRepositoryImpl itemRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private CommentRepositoryImpl commentRepository;

    @Test
    void shouldFindAllByAuthorId() {
        User commentator = userRepository.save(new User(1L, "Пользователь 1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь 2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null));
        commentRepository.save(new Comment(1L, "Комментарий 1", item, commentator, LocalDateTime.now()));
        List<Comment> commentList = commentRepository.findAllByAuthorId(1L);
        assertEquals(1, commentList.size());
        assertEquals(1L, commentList.get(0).getId());
        assertEquals("Комментарий 1", commentList.get(0).getText());
        assertEquals("Предмет 1", commentList.get(0).getItem().getName());
        assertEquals("Пользователь 1", commentList.get(0).getAuthor().getName());
    }

    @Test
    void shouldFindAllByItemOwnerId() {
        User commentator = userRepository.save(new User(1L, "Пользователь 1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь 2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null));
        commentRepository.save(new Comment(1L, "Комментарий 1", item, commentator, LocalDateTime.now()));
        List<Comment> commentList = commentRepository.findAllByItemOwnerId(2L);
        assertEquals(1, commentList.size());
        assertEquals(1L, commentList.get(0).getId());
        assertEquals("Комментарий 1", commentList.get(0).getText());
        assertEquals("Предмет 1", commentList.get(0).getItem().getName());
        assertEquals("Пользователь 1", commentList.get(0).getAuthor().getName());
    }

    @Test
    void shouldFindAllByItemId() {
        User commentator = userRepository.save(new User(1L, "Пользователь 1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь 2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null));
        commentRepository.save(new Comment(1L, "Комментарий 1", item, commentator, LocalDateTime.now()));
        List<Comment> commentList = commentRepository.findAllByItemId(1L);
        assertEquals(1, commentList.size());
        assertEquals(1L, commentList.get(0).getId());
        assertEquals("Комментарий 1", commentList.get(0).getText());
        assertEquals("Предмет 1", commentList.get(0).getItem().getName());
        assertEquals("Пользователь 1", commentList.get(0).getAuthor().getName());
    }

    @Test
    void shouldFindAllByItemIn() {
        User commentator = userRepository.save(new User(1L, "Пользователь 1", "email1@mail.ru"));
        User owner = userRepository.save(new User(2L, "Пользователь 2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "Предмет 1", "Описание предмета 1",
                true, owner, null));
        commentRepository.save(new Comment(1L, "Комментарий 1", item, commentator, LocalDateTime.now()));
        List<Comment> commentList = commentRepository.findAllByItemIn(List.of(item));
        assertEquals(1, commentList.size());
        assertEquals(1L, commentList.get(0).getId());
        assertEquals("Комментарий 1", commentList.get(0).getText());
        assertEquals("Предмет 1", commentList.get(0).getItem().getName());
        assertEquals("Пользователь 1", commentList.get(0).getAuthor().getName());
    }
}