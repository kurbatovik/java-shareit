package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendItem;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId);

    Item edit(Item updatedItem, long itemId, long userId);

    ExtendItem getById(long itemId, long userId);

    List<ExtendItem> getAllByUserId(long userId, Pageable pageable);

    List<Item> searchItems(String text, Pageable pageable);

    Comment addComment(Long userId, Long itemId, String commentText);
}
