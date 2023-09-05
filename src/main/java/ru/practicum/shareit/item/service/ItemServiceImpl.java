package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.exceptions.CommentBookerException;
import ru.practicum.shareit.exceptions.ItemExistException;
import ru.practicum.shareit.exceptions.ItemWithWrongOwner;
import ru.practicum.shareit.exceptions.UserExistException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemUtils.CommentMapper;
import ru.practicum.shareit.item.itemUtils.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryImpl;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.utils.ShareItPageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final CommentRepositoryImpl commentRepository;
    private final UserRepositoryImpl userRepository;
    private final BookingRepositoryImpl bookingRepository;
    private final RequestRepositoryImpl requestRepository;
    private static final String SERVICE_LOG = "Сервис предметов получил запрос на {}{}";

    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        log.info(SERVICE_LOG, "добавление предмета: ", itemDto);
        User owner = checkUserExist(ownerId);
        Request request = checkRequestExist(itemDto.getRequestId());
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(
                null, itemDto, owner, request)), List.of());
    }

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        log.info(SERVICE_LOG, "обновление предмета c id: ", itemId);
        User owner = checkUserExist(ownerId);
        Item currentBDItem = checkItemExist(itemId);
        Item item = ItemMapper.toItem(itemId, itemDto, owner, currentBDItem.getRequest());
        if (item.getName() == null) {
            item.setName(currentBDItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(currentBDItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(currentBDItem.getAvailable());
        }
        checkUserIsOwner(itemId, ownerId);
        return ItemMapper.toItemDto(itemRepository.save(item), commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
    }

    public void deleteItem(Long itemId, Long ownerId) {
        log.info(SERVICE_LOG, "удаление предмета с id: ", itemId);
        checkUserExist(ownerId);
        checkUserIsOwner(itemId, ownerId);
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId, Long userId) {
        log.info(SERVICE_LOG, "получение предмета с id: ", itemId);
        Item item = checkItemExist(itemId);

        List<CommentDto> commentList = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        BookingItemDto previousBooking = checkBookingItemDtoList(bookingRepository
                .findNearPreviousBooking(LocalDateTime.now(), userId, itemId));

        BookingItemDto nextBooking = checkBookingItemDtoList(bookingRepository
                .findNearNextBooking(LocalDateTime.now(), userId, itemId));

        return ItemMapper.toItemDtoGetMethod(item, commentList, previousBooking, nextBooking);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        log.info(SERVICE_LOG, "получение предметов по пользователю с id: ", ownerId);
        checkUserExist(ownerId);
        Map<Long, List<CommentDto>> commentMap = commentRepository.findAllByItemOwnerId(ownerId).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));

        Map<Long, BookingItemDto> previouslyBookingMap = bookingRepository
                .findNearPreviousBookings(LocalDateTime.now(), ownerId).stream()
                .collect(Collectors.toMap(BookingItemDto::getItemId, bookingItemDto -> bookingItemDto));

        Map<Long, BookingItemDto> nextBookingMap = bookingRepository
                .findNearNextBookings(LocalDateTime.now(), ownerId).stream()
                .collect(Collectors.toMap(BookingItemDto::getItemId, bookingItemDto -> bookingItemDto));

        Set<Long> order = bookingRepository.findAllByOwnerId(
                ownerId, new ShareItPageable(0, Integer.MAX_VALUE, Sort.unsorted())).stream()
                .map(booking -> booking.getItem().getId())
                .collect(Collectors.toSet());

        Map<Long, Item> itemMap = itemRepository
                .findAllByOwnerId(ownerId, ShareItPageable.checkPageable(from, size, Sort.unsorted())).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        order.addAll(new HashSet<>(itemMap.keySet()));

        return order.stream()
                .map(id -> ItemMapper.toItemDtoGetMethod(
                        itemMap.get(id), commentMap.get(id), previouslyBookingMap.getOrDefault(id, null),
                        nextBookingMap.getOrDefault(id, null)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByName(String text, Integer from, Integer size) {
        log.info(SERVICE_LOG, "получение предметов по названию: ", text);
        if (text.isBlank()) {
            return List.of();
        }
        Map<Long, Item> itemMap = itemRepository.findAllByText(text,
                        ShareItPageable.checkPageable(from, size, Sort.unsorted())).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        Map<Long, List<Comment>> commentMap = commentRepository.findAllByItemIn(
                new ArrayList<>(itemMap.values())).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemMap.values().stream()
                .map(item -> ItemMapper.toItemDto(item, commentMap.getOrDefault(item.getId(), List.of()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public CommentDto addCommentToItem(Long itemId, CommentDto commentDto, Long authorId) {
        log.info(SERVICE_LOG, "добавление комментария: ", commentDto.getText());
        commentDto.setCreated(LocalDateTime.now());
        Item item = checkItemExist(itemId);
        User author = checkUserExist(authorId);
        bookingRepository.findAllPastByBookerId(authorId, LocalDateTime.now(), new ShareItPageable(
                0, Integer.MAX_VALUE, Sort.unsorted())).stream()
                .filter(booking -> booking.getItem().getId().longValue() == itemId).findFirst()
                .orElseThrow(() -> new CommentBookerException("Ошибка. Комментарий может оставить бывший арендатор"));
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper
                .toComment(commentDto, item, author)));
    }

    private User checkUserExist(Long ownerId) {
        log.info("Начата процедура проверки наличия в репозитории пользователя с id: {}", ownerId);
        return userRepository.findById(ownerId).orElseThrow(
                () -> new UserExistException("Ошибка. Запрошенного пользователя в базе данных не существует"));
    }

    private Item checkItemExist(Long itemId) {
        log.info("Начата процедура проверки наличия в репозитории предмета с id: {}", itemId);
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ItemExistException("Ошибка. Запрошенного предмета в базе данных не существует"));
    }

    private void checkUserIsOwner(Long itemId, Long ownerId) {
        log.info("Начата процедура проверки принадлежности предмета с id: {} пользователю с id: {}", itemId, ownerId);
        if (!itemRepository.findById(itemId).orElseThrow(
                () -> new ItemExistException("Ошибка. Запрошенного предмета в базе данных не существует"))
                .getOwner().getId().equals(ownerId)) {
            throw new ItemWithWrongOwner("Ошибка. Обновить/удалить предмет может только владелец предмета");
        }
    }

    private BookingItemDto checkBookingItemDtoList(List<BookingItemDto> bookinglist) {
        if (bookinglist.size() == 0) {
            return null;
        } else {
            return bookinglist.get(0);
        }
    }

    private Request checkRequestExist(Long requestId) {
        log.info("Начата процедура проверки наличия в репозитории запроса с id: {}", requestId);
        if (requestId == null) {
            return null;
        }
        return requestRepository.findById(requestId).orElse(null);
    }
}