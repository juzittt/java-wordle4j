package ru.yandex.practicum.exception.system;

import java.io.FileNotFoundException;
import java.io.Serial;

public class FileNotFound extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 112L;

    public FileNotFound(FileNotFoundException cause) {
        super(cause);
    }
}