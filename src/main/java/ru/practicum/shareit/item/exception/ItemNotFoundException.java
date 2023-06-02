package ru.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(final String message) {
        super(message);
    }
}
