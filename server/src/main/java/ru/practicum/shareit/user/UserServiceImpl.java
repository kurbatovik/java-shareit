package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    @Override
    public User edit(Long id, User updatedUser) {
        boolean isUpdated = false;
        User user = returnUserOrThrowUserNotFoundException(id);
        log.info("Update: {}", user);
        String updatedUserEmail = updatedUser.getEmail();
        if (updatedUserEmail != null && !updatedUserEmail.equals(user.getEmail()) && !updatedUserEmail.isBlank()) {
            user.setEmail(updatedUserEmail);
            log.info("Update email address");
            isUpdated = true;
        }
        String updatedUserName = updatedUser.getName();
        if (updatedUserName != null && !updatedUserName.equals(user.getName()) && !updatedUserName.isBlank()) {
            user.setName(updatedUser.getName());
            log.info("Update user name");
            isUpdated = true;
        }
        if (isUpdated) {
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return returnUserOrThrowUserNotFoundException(id);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User returnUserOrThrowUserNotFoundException(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(
                () -> {
                    log.info("Throw new NotFoundException");
                    return new NotFoundException(Variables.USER_WITH_ID_NOT_FOUND, id);
                }
        );
    }
}
