package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.EmailAlreadyExist;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleEmailException(final EmailAlreadyExist e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUserAccessException(final UserAccessException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.FORBIDDEN);
    }

}
