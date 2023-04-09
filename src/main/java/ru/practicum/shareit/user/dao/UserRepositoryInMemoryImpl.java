package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryInMemoryImpl implements UserRepository {

    private static long id = 0;
    private final HashMap<String, Long> emails = new HashMap<>();
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> findByEmail(String email) {
        Long id = emails.get(email);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> save(User user) {
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return Optional.of(user);
    }

    @Override
    public Optional<User> create(User user) {
        User newUser = User.builder().id(++id).email(user.getEmail()).name(user.getName()).build();
        return save(newUser);
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(long id) {
        User deleteUser = users.get(id);
        if (deleteUser != null) {
            emails.remove(deleteUser.getEmail());
            users.remove(id);
        }
    }

    @Override
    public void removeEmail(String email) {
        emails.remove(email);
    }
}
