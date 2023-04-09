package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private static long id = 0;
    private final Map<Long, Item> items = new HashMap<>();
    private final MultiValueHashMap<User, Item> userItems = new MultiValueHashMap<>();

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Optional<Item> save(Item item) {
        items.put(item.getId(), item);
        userItems.put(item.getOwner(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> create(Item item) {
        Item newItem = Item.builder().id(++id).name(item.getName()).description(item.getDescription())
                .owner(item.getOwner()).available(item.getAvailable()).request(item.getRequest()).build();
        return save(newItem);
    }

    @Override
    public Optional<Item> update(Item item) {
        return save(item);
    }

    @Override
    public void delete(long id) {
        Item deleteItem = items.get(id);
        if (deleteItem != null) {
            items.remove(id);
            userItems.remove(deleteItem.getOwner(), deleteItem);

        }
    }

    @Override
    public List<Item> findAllByOwner(User user) {
        return userItems.get(user);
    }

    @Override
    public List<Item> findAllByNameOrDescription(String text) {
        return items.values().stream().filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(text)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(text))
                .collect(Collectors.toList());
    }

    static class MultiValueHashMap<K, V> {
        private final Map<K, List<V>> map = new HashMap<>();

        public void put(K key, V value) {
            List<V> values = map.computeIfAbsent(key, k -> new ArrayList<>());
            if (!values.contains(value)) {
                values.add(value);
            }
        }

        public List<V> get(K key) {
            return map.get(key);
        }

        public void remove(K key, V value) {
            List<V> values = map.get(key);
            if (values != null) {
                values.remove(value);
            }
        }
    }
}
