package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto edit(long itemId, long userId, ItemDto itemDto);

    ItemDto getById(long itemId);

    List<ItemDto> getAllByUserId(long userId);

    List<ItemDto> searchItems(String text);
}
