package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Добавление новой вещи
    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                       @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.info("Request on add {}, user id: {}", itemDto, userId);
        ItemDto newItem = itemService.add(userId, itemDto);
        return ResponseEntity.ok(newItem);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> edit(@PathVariable @Positive Long itemId,
                                        @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                        @RequestBody ItemDto itemDto) {
        log.info("Request on edit item id: {}, {}, user id: {}", itemId, itemDto, userId);
        ItemDto editedItem = itemService.edit(itemId, userId, itemDto);
        return ResponseEntity.ok(editedItem);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@PathVariable @Positive Long itemId) {
        log.info("Request on get item. ID: {}", itemId);
        ItemDto itemDto = itemService.getById(itemId);
        return ResponseEntity.ok(itemDto);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Request on get all for user id: {}", userId);
        List<ItemDto> items = itemService.getAllByUserId(userId);
        return ResponseEntity.ok(items);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("Request on search text: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<ItemDto> items = itemService.searchItems(text.toLowerCase(Locale.ROOT));
        return ResponseEntity.ok(items);
    }

}
