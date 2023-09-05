package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.RequestExistException;
import ru.practicum.shareit.exceptions.UserExistException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.request.dto.ReceivedRequestDto;
import ru.practicum.shareit.request.dto.ReturnRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.request.requestUtils.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepositoryImpl requestRepository;
    private final UserRepositoryImpl userRepository;
    private final ItemRepositoryImpl itemRepository;
    private static final String SERVICE_LOG = "Сервис запросов получил запрос на {}{}";

    public ReturnRequestDto addRequest(ReceivedRequestDto requestDto, Long requestorId) {
        log.info(SERVICE_LOG, "Добавление запроса: ", requestDto);
        User requestor = checkUserExist(requestorId);
        return RequestMapper.toRequestDto(
                requestRepository.save(RequestMapper.toRequest(requestDto, requestor, LocalDateTime.now())),
                List.of());
    }

    @Transactional(readOnly = true)
    public List<ReturnRequestDto> getOthersRequests(Integer from, Integer size, Long userId) {
        log.info(SERVICE_LOG, "Получение запросов постранично начиная с: ", from);
        checkUserExist(userId);
        List<Request> requestList = requestRepository.findAllForUser(
                userId,
                ShareItPageable.checkPageable(from, size, Sort.by("created").descending())).toList();
        return getUniqueOperations(requestList);
    }

    @Transactional(readOnly = true)
    public ReturnRequestDto getRequest(Long requestId, Long userId) {
        log.info(SERVICE_LOG, "Получение запроса с id: ", requestId);
        checkUserExist(userId);
        return RequestMapper.toRequestDto(
                requestRepository.findById(requestId).orElseThrow(
                        () -> new RequestExistException("Ошибка. Запрошенного запроса в базе данных не существует")),
                (itemRepository.findAllByRequestsId(List.of(requestId))).stream()
                        .map(ItemMapper::toItemRequestDto)
                        .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<ReturnRequestDto> getUserRequests(Long userId) {
        log.info(SERVICE_LOG, "Получение всех запросов пользователя: ", userId);
        checkUserExist(userId);
        List<Request> requestList = requestRepository.findRequestsByRequestorId(userId);
        return getUniqueOperations(requestList);
    }

    private User checkUserExist(Long ownerId) {
        log.info("Начата процедура проверки наличия в репозитории пользователя с id: {}", ownerId);
        return userRepository.findById(ownerId).orElseThrow(
                () -> new UserExistException("Ошибка. Запрошенного пользователя в базе данных не существует"));
    }

    private List<ReturnRequestDto> getUniqueOperations(List<Request> requestList) {
        Map<Long, List<ItemRequestDto>> itemMap = itemRepository.findAllByRequestsId(
                        requestList.stream().map(Request::getId).collect(Collectors.toList())).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemRequestDto, Collectors.toList())));

        return requestList.stream()
                .map(request -> RequestMapper.toRequestDto(request, itemMap.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }
}