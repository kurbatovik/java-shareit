package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    protected User fromDto(UserDto userDto) {
        return User.builder().id(userDto.getId()).email(userDto.getEmail()).name(userDto.getName()).build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).build();
    }
}
