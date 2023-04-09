package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {


    List<Item> findAll();

    Optional<Item> findById(Long id);

    Optional<Item> save(Item item);

    Optional<Item> create(Item item);

    Optional<Item> update(Item item);

    void delete(long id);

    List<Item> findAllByOwner(User user);

    List<Item> findAllByNameOrDescription(String text);
}
