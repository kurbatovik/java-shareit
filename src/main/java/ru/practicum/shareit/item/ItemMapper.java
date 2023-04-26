package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public final BookingMapper bookingMapper;

    public ItemDto toDto(Item item) {
        return ItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).build();
    }


    public Item fromDto(ItemDto itemDto, User owner) {
        return Item.builder().id(itemDto.getId()).name(itemDto.getName()).description(itemDto.getDescription())
                .available(itemDto.getAvailable()).owner(owner).build();
    }

    public OwnerItemDto toDtoOwner(Item item, Booking lastBooking, Booking nextBooking, List<Comment> commentsList) {
        List<CommentDto> comments = commentsList.stream().map(
                this::toCommentDto).collect(Collectors.toList());
        OwnerItemDto itemDto = OwnerItemDto.builder().id(item.getId()).name(item.getName()).comments(comments)
                .description(item.getDescription()).available(item.getAvailable()).build();
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.getShortDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.getShortDto(nextBooking));
        }
        return itemDto;
    }

    public CommentDto toCommentDto(Comment c) {
        return CommentDto.builder().id(c.getId()).text(c.getText()).authorName(c.getAuthor().getName())
                .created(c.getCreated()).build();
    }
}
