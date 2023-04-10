package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveErrorException;
import ru.practicum.shareit.exception.UserFoundException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus
    public ErrorResponse throwableHandler(final Exception e) {
        log.info("Server error {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(UserFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleException(final UserFoundException e) {
        log.info("Conflict: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(SaveErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final SaveErrorException e) {
        log.info("Internal server error: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final NotFoundException e) {
        log.info("Not found: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = ((FieldError) error).getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.info("Validation failed for field: {}. Error message: {}", fieldName, errorMessage);
        });
        return ErrorResponse.builder().message("Validation failed").errors(errors).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach((error) -> {
            String fieldName = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
            log.info("Validation failed for field: {}. Error message: {}", fieldName, errorMessage);
        });
        return ErrorResponse.builder().message("Validation failed").errors(errors).build();
    }
}
