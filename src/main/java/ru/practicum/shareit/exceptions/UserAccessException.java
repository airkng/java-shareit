package ru.practicum.shareit.exceptions;

public class UserAccessException extends RuntimeException {
    public UserAccessException() {
        super();
    }

    public UserAccessException(final String message) {
        super(message);
    }
}
