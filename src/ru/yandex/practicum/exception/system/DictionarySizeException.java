package ru.yandex.practicum.exception.system;

import java.io.Serial;

public class DictionarySizeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 111L;

    public DictionarySizeException(String message) {
        super(message);
    }
}