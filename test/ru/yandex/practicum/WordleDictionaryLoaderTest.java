package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.system.FileNotFound;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Tag("loader")
@DisplayName("Тесты WordleDictionaryLoader — загрузка и фильтрация слов из файла")
public class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    private PrintWriter sysWriter;

    @BeforeEach
    void setUp() {
        sysWriter = new PrintWriter(System.out);
    }

    @Test
    @DisplayName("Должен выбросить FileNotFound при отсутствии файла")
    void shouldThrowFileNotFoundWhenFileDoesNotExist() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(sysWriter);
        assertThrows(FileNotFound.class, () -> loader.wordsFileLoader("nonexistent.txt"));
    }

    @Test
    @DisplayName("Загружает только 5-буквенные слова")
    void shouldLoadOnlyFiveLetterWords() throws IOException {
        File tempFile = tempDir.resolve("words.txt").toFile();
        try (var writer = new PrintWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.println("мама");
            writer.println("домик");
            writer.println("кошка");
            writer.println("привет");
        }

        WordleDictionaryLoader loader = new WordleDictionaryLoader(sysWriter);
        WordleDictionary dict = loader.wordsFileLoader(tempFile.getAbsolutePath());

        assertTrue(dict.contains("домик"));
        assertTrue(dict.contains("кошка"));
        assertFalse(dict.contains("мама"));
        assertFalse(dict.contains("привет"));
        assertEquals(2, dict.size());
    }

    @Test
    @DisplayName("Обрабатывает пустой файл без ошибок")
    void shouldHandleEmptyFile() throws IOException {
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        WordleDictionaryLoader loader = new WordleDictionaryLoader(sysWriter);
        WordleDictionary dict = loader.wordsFileLoader(tempFile.getAbsolutePath());

        assertTrue(dict.getDictionaryList().isEmpty());
    }

}