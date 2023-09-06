package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;

import java.util.List;

public interface RequestService {
    ReturnRequestDto addRequest(ReceivedRequestDto requestDto, Long requestorId);

    List<ReturnRequestDto> getUserRequests(Long requestorId);

    List<ReturnRequestDto> getOthersRequests(Integer from, Integer size, Long requestorId);

    ReturnRequestDto getRequest(Long requestId,  Long requestorId);
}
