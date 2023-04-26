package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    // Добавление новой вещи
    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                       @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.info("Request on add {}, user id: {}", itemDto, userId);
        return ResponseEntity.ok(itemService.add(userId, itemDto));
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> edit(@PathVariable @Positive Long itemId,
                                        @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                        @RequestBody ItemDto itemDto) {
        log.info("Request on edit item id: {}, {}, user id: {}", itemId, itemDto, userId);
        return ResponseEntity.ok(itemService.edit(itemId, userId, itemDto));
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ResponseEntity<OwnerItemDto> getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @PathVariable @Positive Long itemId) {
        log.info("Request from user ID: {} on get item. ID: {}", userId, itemId);
        return ResponseEntity.ok(itemService.getById(itemId, userId));
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public ResponseEntity<List<OwnerItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Request on get all for user id: {}", userId);
        List<OwnerItemDto> allByUserId = itemService.getAllByUserId(userId);
        System.out.println(false);
        return ResponseEntity.ok(allByUserId);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("Request on search text: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(itemService.searchItems(text.toLowerCase(Locale.ROOT)));
    }

    // Добавление комментария
    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                 @RequestBody @Validated(Create.class) CommentDto commentDto,
                                                 @PathVariable @Positive Long itemId) {
        log.info("Request on add comment {}, item id: {}, user id: {}", commentDto.getText(), itemId, userId);
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto.getText()));
    }
}
