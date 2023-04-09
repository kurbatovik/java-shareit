package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return ItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).build();
    }


    public Item fromDto(ItemDto itemDto, User owner) {
        return Item.builder().id(itemDto.getId()).name(itemDto.getName()).description(itemDto.getDescription())
                .available(itemDto.getAvailable()).owner(owner).build();
    }
}
