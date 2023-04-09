package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    protected UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    //Просмотр всех пользователей.
    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Request on get all");
        return ResponseEntity.ok(service.getAll());
    }

    //Просмотр пользователя по идентификатору
    @GetMapping(value = "{id}")
    public ResponseEntity<UserDto> getById(@PathVariable @Positive(message = "ID should be positive") Long id) {
        log.info("Request to get user with ID: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    //Добавление нового пользователя
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "")
    public ResponseEntity<UserDto> add(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.debug("Request to create: {}", userDto);
        UserDto newUser = service.add(userDto);
        log.info("Has been created: {}", newUser);
        return ResponseEntity.ok(newUser);
    }

    //Редактирование нового пользователя
    @PatchMapping(value = "{id}")
    public ResponseEntity<UserDto> edit(@Validated(Update.class) @RequestBody UserDto userDto,
                                        @PathVariable @Positive(message = "ID should be positive") Long id) {
        log.debug("Request to updated: {}", userDto);
        UserDto updatedUser = service.edit(id, userDto);
        log.info("Has been updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    //Удаление полььзователя
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable @Positive(message = "ID should be positive") Long id) {
        log.info("Request to delete with id: {}", id);
        service.delete(id);
    }
}
