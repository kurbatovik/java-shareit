package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendItem;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;

    // Добавление новой вещи
    @PostMapping
    public ItemDto add(@RequestHeader(Variables.USER_ID) Long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Request on add {}, user id: {}", itemDto, userId);
        Item item = itemService.add(itemMapper.fromDto(itemDto), userId);
        return itemMapper.toDto(item);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId,
                        @RequestHeader(Variables.USER_ID) Long userId,
                        @RequestBody ItemDto itemDto) {
        log.info("Request on edit item id: {}, {}, user id: {}", itemId, itemDto, userId);
        Item item = itemService.edit(itemMapper.fromDto(itemDto), itemId, userId);
        return itemMapper.toDto(item);
    }

    // Просмотр информации о конкретной вещи по её идентификатору,
    // если смотрит владелец добавить последнее и будущее бронирование
    @GetMapping("/{itemId}")
    public OwnerItemDto getById(@RequestHeader(Variables.USER_ID) Long userId,
                                @PathVariable Long itemId) {
        log.info("Request from user ID: {} on get item. ID: {}", userId, itemId);
        ExtendItem item = itemService.getById(itemId, userId);
        return itemMapper.toDtoOwner(item);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описанием для каждой
    @GetMapping
    public List<OwnerItemDto> getAll(@RequestHeader(Variables.USER_ID) Long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = Util.getPageable(from, size);
        log.info("Request on get all for user id: {}", userId);
        List<ExtendItem> allByUserId = itemService.getAllByUserId(userId, pageable);
        return allByUserId.stream()
                .map(itemMapper::toDtoOwner)
                .collect(Collectors.toList());
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = Util.getPageable(from, size);
        log.info("Request on search text: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemService.searchItems(text.toLowerCase(Locale.ROOT), pageable);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    // Добавление комментария
    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader(Variables.USER_ID) Long userId,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId) {
        log.info("Request on add comment {}, item id: {}, user id: {}", commentDto.getText(), itemId, userId);
        Comment comment = itemService.addComment(userId, itemId, commentDto.getText());
        return itemMapper.toCommentDto(comment);
    }
}
