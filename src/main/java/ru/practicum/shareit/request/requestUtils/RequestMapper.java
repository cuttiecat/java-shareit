package ru.practicum.shareit.request.requestUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@UtilityClass
public class RequestMapper {
    public ReturnRequestDto toRequestDto(Request request, List<ItemRequestDto> itemList) {
        log.info("Начата процедура преобразования запроса в ДТО: {}", request);
        return new ReturnRequestDto(request.getId(), request.getDescription(), itemList, request.getCreated());
    }

    public Request toRequest(ReceivedRequestDto requestDto, User user, LocalDateTime created) {
        log.info("Начата процедура преобразования ДТО в запрос: {}", requestDto);
        return new Request(null, requestDto.getDescription(), user, created);
    }
}
