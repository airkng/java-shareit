package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

import javax.validation.ConstraintViolationException;
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

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotAvailableException(final NotAvailableException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongState(final StateNotSupportException e) {
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintException(final ConstraintViolationException e) {
        return new ResponseEntity<>(Map.of("Exception: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }


}
