package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveErrorException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemMapper.fromDto(itemDto, user);
        Item savedItem = itemRepository.create(item).orElseThrow(
                () -> new SaveErrorException("Item is not save: {0}", item)
        );
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto edit(long itemId, long userId, ItemDto itemDto) {
        boolean isUpdated = false;
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        log.info("Update: {}", item);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User is not the owner of the item");
        }
        Item updatedItem = itemMapper.fromDto(itemDto, item.getOwner());
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
            itemRepository.update(item).orElseThrow(
                    () -> new SaveErrorException("Item is not save: {0}", item)
            );
        }
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Item> items = itemRepository.findAllByOwner(user);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemRepository.findAllByNameOrDescription(text);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}