package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<ReceivedBookingDto> jsonTester;

    @Test
    void shouldCheckParse() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        ReceivedBookingDto bookingDto = new ReceivedBookingDto(null, start, end);
        JsonContent<ReceivedBookingDto> jsonContent = jsonTester.write(bookingDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
    }
}
