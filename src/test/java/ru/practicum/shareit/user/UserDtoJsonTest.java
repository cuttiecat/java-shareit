package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> jsonTester;

    @Test
    void shouldCheckParse() throws Exception {
        UserDto userDto = new UserDto(null, "Пользователь 1", "email1@mail.ru", List.of());
        JsonContent<UserDto> jsonContent = jsonTester.write(userDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Пользователь 1");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("email1@mail.ru");
        assertThat(jsonContent).extractingJsonPathValue("$.comments").isEqualTo(List.of());
    }
}
