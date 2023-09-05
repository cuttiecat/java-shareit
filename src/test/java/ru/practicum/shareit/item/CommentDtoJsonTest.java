package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jsonTester;

    @Test
    void shouldCheckParse() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Комментарий", null, null);
        assertThat(jsonTester.write(commentDto)).extractingJsonPathStringValue("$.text")
                .isEqualTo("Комментарий");
    }
}
