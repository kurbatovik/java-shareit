package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    //Просмотр всех пользователей.
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Request on get all");
        List<UserDto> dtoList = userService.getAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    //Просмотр пользователя по идентификатору
    @GetMapping("{id}")
    public ResponseEntity<UserDto> getById(@PathVariable @Positive(message = "ID should be positive") Long id) {
        log.info("Request to get user with ID: {}", id);
        UserDto userDto = userMapper.toDto(userService.getById(id));
        return ResponseEntity.ok(userDto);
    }

    //Добавление нового пользователя
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<UserDto> add(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.debug("Request to create: {}", userDto);
        User newUser = userService.add(userMapper.fromDto(userDto));
        log.info("Has been created: {}", newUser);
        return ResponseEntity.ok(userMapper.toDto(newUser));
    }

    //Редактирование нового пользователя
    @PatchMapping("{id}")
    public ResponseEntity<UserDto> edit(@Validated(Update.class) @RequestBody UserDto userDto,
                                        @PathVariable @Positive(message = "ID should be positive") Long id) {
        log.debug("Request to updated: {}", userDto);
        User updatedUser = userService.edit(id, userMapper.fromDto(userDto));
        log.info("Has been updated: {}", updatedUser);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    //Удаление пользователя
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable @Positive(message = "ID should be positive") Long id) {
        log.info("Request to delete with id: {}", id);
        userService.delete(id);
    }
}
