package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.validate.BadWordLengthException;
import ru.yandex.practicum.exception.validate.WordNotFoundException;
import ru.yandex.practicum.exception.validate.WrongLanguageException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("main")
@DisplayName("Тесты Wordle — главный класс, ввод-вывод, логгирование")
public class WordleTest {

    @TempDir
    Path tempDir;

    private File wordsFile;
    private PrintWriter sysWriter;
    private PrintWriter valWriter;

    @BeforeEach
    void setUp() throws IOException {
        wordsFile = tempDir.resolve("words_ru.txt").toFile();
        try (var w = new PrintWriter(wordsFile, StandardCharsets.UTF_8)) {
            w.println("мечта");
            w.println("бочка");
        }

        sysWriter = new PrintWriter(Files.createTempFile(tempDir, "sys", ".log").toFile());
        valWriter = new PrintWriter(Files.createTempFile(tempDir, "val", ".log").toFile());
    }

    @AfterEach
    void tearDown() {
        sysWriter.close();
        valWriter.close();
    }

    @Test
    @DisplayName("Выбрасывает WrongLanguageException при латинице")
    void shouldThrowWrongLanguageExceptionOnInvalidInput() {
        String input = "hello\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Wordle wordle = new Wordle(sysWriter, valWriter, in);

        WrongLanguageException exception = assertThrows(
                WrongLanguageException.class,
                () -> wordle.startGame(true),
                "Ожидалось исключение WrongLanguageException"
        );

        assertTrue(exception.getMessage().contains("только русские буквы"));
    }

    @Test
    @DisplayName("Выбрасывает BadWordLengthException при слове ≠ 5 букв")
    void shouldThrowBadWordLengthExceptionOnShortWord() {
        String input = "мама\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Wordle wordle = new Wordle(sysWriter, valWriter, in);

        BadWordLengthException exception = assertThrows(
                BadWordLengthException.class,
                () -> wordle.startGame(true),
                "Ожидалось исключение BadWordLengthException"
        );

        assertTrue(exception.getMessage().contains("из 5 букв"));
    }

    @Test
    @DisplayName("Выбрасывает WordNotFoundException при слове не в словаре")
    void shouldThrowWordNotFoundExceptionOnUnknownWord() {
        String input = "транг\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Wordle wordle = new Wordle(sysWriter, valWriter, in);

        WordNotFoundException exception = assertThrows(
                WordNotFoundException.class,
                () -> wordle.startGame(true),
                "Ожидалось исключение WordNotFoundException"
        );

        assertTrue(exception.getMessage().contains("не найдено"));
    }
}