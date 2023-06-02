package ru.practicum.shareit.user.exception;

public class EmailAlreadyExist extends RuntimeException {
    public EmailAlreadyExist() {
        super();
    }

    public EmailAlreadyExist(final String message) {
        super(message);
    }
}
