package ru.practicum.shareit.exceptions;

public class StateNotSupportException extends RuntimeException {
    public StateNotSupportException() {
    }

    public StateNotSupportException(final String message) {
        super(message);
    }
}
