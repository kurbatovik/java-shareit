package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> save(User user);

    Optional<User> create(User user);

    void update(User user);

    void delete(long id);

    void removeEmail(String email);
}
