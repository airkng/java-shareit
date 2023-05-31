package ru.practicum.shareit.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleEmailException(final EmailAlreadyExist e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(final UserNotFoundException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

}
