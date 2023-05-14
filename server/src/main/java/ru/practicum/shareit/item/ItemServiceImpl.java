package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item add(Item item, long userId) {
        User owner = getOwnerOrThrowNotFoundException(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item edit(Item updatedItem, long itemId, long userId) {
        boolean isUpdated = false;
        Item item = getItemOrThrowNotFoundException(itemId);
        log.info("Update: {}", item);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User is not the owner of the item");
        }
        String updatedItemName = updatedItem.getName();
        if (updatedItemName != null && !updatedItemName.equals(item.getName())) {
            item.setName(updatedItemName);
            log.info("Update name");
            isUpdated = true;
        }
        String updatedItemDescription = updatedItem.getDescription();
        if (updatedItemDescription != null && !item.getDescription().equals(updatedItemDescription)) {
            item.setDescription(updatedItemDescription);
            log.info("Update description");
            isUpdated = true;
        }
        if (updatedItem.getAvailable() != null && item.getAvailable() != updatedItem.getAvailable()) {
            item.setAvailable(updatedItem.getAvailable());
            log.info("Update available");
            isUpdated = true;
        }
        if (isUpdated) {
            item = itemRepository.save(item);
        }
        return item;
    }

    @Override
    public ExtendItem getById(long itemId, long userId) {
        ExtendItem item = new ExtendItem(getItemOrThrowNotFoundException(itemId));
        item.setComments(commentRepository.findByItemId(itemId));
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            item.setNextBooking(
                    bookingRepository
                            .findFirstByItemAndStartAfterAndStatusOrderByStart(item, now, BookingStatus.APPROVED)
                            .orElse(null));
            item.setLastBooking(
                    bookingRepository
                            .findFirstByItemAndStartLessThanEqualAndStatusOrderByStartDesc(item, now,
                                    BookingStatus.APPROVED)
                            .orElse(null));
        }
        return item;
    }

    @Override
    public List<ExtendItem> getAllByUserId(long userId, Pageable pageable) {
        User user = getOwnerOrThrowNotFoundException(userId);
        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(user, pageable).toList();
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        LocalDateTime now = LocalDateTime.now();
        Map<Item, Booking> lastBookings = bookingRepository
                .findFirstByItemInAndStartLessThanEqualAndStatusOrderByStartDesc(items, now, BookingStatus.APPROVED)
                .stream()
                .collect(toMap(Booking::getItem, identity()));
        Map<Item, Booking> nextBookings = bookingRepository
                .findFirstByItemInAndStartAfterAndStatusOrderByStart(items, now, BookingStatus.APPROVED)
                .stream()
                .collect(toMap(Booking::getItem, identity()));
        return items.stream().map(item -> new ExtendItem(item)
                .setComments(comments.getOrDefault(item, Collections.emptyList()))
                .setLastBooking(lastBookings.get(item))
                .setNextBooking(nextBookings.get(item))).collect(toList());
    }

    @Override
    public List<Item> searchItems(String text, Pageable pageable) {
        return itemRepository.findLikingByNameOrDescription(text, pageable).toList();
    }

    @Override
    public Comment addComment(Long userId, Long itemId, String commentText) {
        User user = getOwnerOrThrowNotFoundException(userId);
        Item item = getItemOrThrowNotFoundException(itemId);
        LocalDateTime now = LocalDateTime.now();
        bookingRepository
                .findFirstByItemAndBookerAndStartBeforeAndStatusOrderByStartDesc(item, user, now, BookingStatus.APPROVED)
                .orElseThrow(() -> new NotAvailableException("Booking is not available for user id: {0} and item id: {1}",
                        userId, itemId));
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setCreated(now);
        comment.setItem(item);
        comment.setText(commentText);
        return commentRepository.save(comment);
    }

    private Item getItemOrThrowNotFoundException(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    private User getOwnerOrThrowNotFoundException(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Variables.USER_WITH_ID_NOT_FOUND, userId));
    }
}