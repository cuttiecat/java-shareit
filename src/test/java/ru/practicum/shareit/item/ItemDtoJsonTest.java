package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jsonTester;

    @Test
    void shouldCheckParse() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Предмет", "Описание предмета",
                true, List.of(), null, null, 4L);
        JsonContent<ItemDto> jsonContent = jsonTester.write(itemDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Предмет");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("Описание предмета");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathValue("$.comments").isEqualTo(List.of());
        assertThat(jsonContent).extractingJsonPathValue("$.lastBooking").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathValue("$.nextBooking").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(4);
    }
}
