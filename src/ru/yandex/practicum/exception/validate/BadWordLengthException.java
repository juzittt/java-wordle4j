package ru.yandex.practicum.exception.validate;

import java.io.Serial;

public class BadWordLengthException extends Exception {
    @Serial
    private static final long serialVersionUID = 221L;

    public BadWordLengthException(String message) {
        super(message);
    }
}