package ru.practicum.shareit.booking;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;


public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    ApplicationContext context = new AnnotationConfigApplicationContext(SpringBootConfiguration.class);

    WebClientConfig webClientConfig = context.getBean(WebClientConfig.class);

    public BookingClient(RestTemplate rest) {
        super(rest);
    }


//    @Value("(\"${shareit-server.url}\")")
//    private String serverUrl;
//    @Autowired
//    public BookingClient(String serverUrl, RestTemplateBuilder builder) {
//        super(
//                builder
//                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
//                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
//                        .build()
//        );
//    }


    public ResponseEntity<Object> addBooking(Long userId, BookingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByBookerId(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsForAllItemsByOwnerId(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
