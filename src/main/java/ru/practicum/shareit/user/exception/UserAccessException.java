package ru.practicum.shareit.user.exception;

public class UserAccessException extends RuntimeException {
    public UserAccessException() {
        super();
    }

    public UserAccessException(final String message) {
        super(message);
    }
}
