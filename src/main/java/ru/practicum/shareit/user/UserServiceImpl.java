package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveErrorException;
import ru.practicum.shareit.exception.UserFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto add(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        userRepository.findByEmail(user.getEmail()).ifPresent(this::throwExceptionWhenUserIsPresent);
        User newUser = userRepository.create(user).orElseThrow(
                () -> new SaveErrorException("User is not save: {0}", user)
        );
        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto edit(Long id, UserDto userDto) {
        boolean isUpdated = false;
        User updatedUser = userMapper.fromDto(userDto);
        User user = returnUserOrThrowUserNotFoundException(id);
        log.info("Update: {}", user);
        String updatedUserEmail = updatedUser.getEmail();
        if (updatedUserEmail != null && !updatedUserEmail.equals(user.getEmail())) {
            userRepository.findByEmail(updatedUserEmail).filter(u -> u.getId() != user.getId())
                    .ifPresent(this::throwExceptionWhenUserIsPresent);
            userRepository.removeEmail(user.getEmail());
            user.setEmail(updatedUserEmail);
            log.info("Update email address");
            isUpdated = true;
        }
        String updatedUserName = updatedUser.getName();
        if (updatedUserName != null && !updatedUserName.equals(user.getName())) {
            user.setName(updatedUser.getName());
            log.info("Update user name");
            isUpdated = true;
        }
        if (isUpdated) {
            userRepository.update(user);
        }
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toDto(returnUserOrThrowUserNotFoundException(id));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private User returnUserOrThrowUserNotFoundException(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(
                () -> {
                    log.info("Throw new NotFoundException");
                    return new NotFoundException("User with ID: {0} not found", id);
                }
        );
    }

    private void throwExceptionWhenUserIsPresent(User user) {

        String error = MessageFormat.format("User with this email address is found. ID: {0}, email: {1}",
                user.getId(), user.getEmail());
        log.info("Throw new AlreadyExistException");
        throw new UserFoundException(error);
    }
}
