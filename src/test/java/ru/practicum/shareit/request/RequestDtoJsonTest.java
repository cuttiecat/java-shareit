package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<ReceivedRequestDto> jsonTester;

    @Test
    void shouldCheckParse() throws Exception {
        ReceivedRequestDto requestDto = new ReceivedRequestDto("Описание запроса");
        assertThat(jsonTester.write(requestDto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Описание запроса");
    }
}
