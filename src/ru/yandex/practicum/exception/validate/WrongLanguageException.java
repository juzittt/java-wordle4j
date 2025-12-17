package ru.yandex.practicum.exception.validate;

import java.io.Serial;

public class WrongLanguageException extends Exception {
    @Serial
    private static final long serialVersionUID = 223L;

    public WrongLanguageException(String message) {
        super(message);
    }
}