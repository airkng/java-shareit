package ru.practicum.shareit.exceptions;

public class EmailAlreadyExist extends RuntimeException {
    public EmailAlreadyExist(final String message) {
        super(message);
    }
}
