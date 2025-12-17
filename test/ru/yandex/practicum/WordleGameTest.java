package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.system.DictionarySizeException;
import ru.yandex.practicum.exception.validate.BadWordLengthException;
import ru.yandex.practicum.exception.validate.WordNotFoundException;
import ru.yandex.practicum.exception.validate.WrongLanguageException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

@Tag("game")
@DisplayName("Тесты WordleGame — логика игры и обработка попыток")
public class WordleGameTest {

    private WordleDictionaryLoader loader;
    private PrintWriter sysWriter;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        sysWriter = new PrintWriter(new StringWriter());

        WordleDictionary dict = new WordleDictionary();
        dict.addWord("мечта");
        dict.addWord("бочка");

        loader = new WordleDictionaryLoader(sysWriter) {
            @Override
            public WordleDictionary wordsFileLoader(String fileName) {
                return dict;
            }
        };

        game = new WordleGame(loader, sysWriter) {{
            answer = "мечта";
        }};
    }

    @Test
    @DisplayName("Выбрасывает WrongLanguageException при не-кириллических символах")
    void shouldThrowWrongLanguageExceptionOnNonCyrillic() {
        WrongLanguageException ex = assertThrows(WrongLanguageException.class, () -> game.tryToGuessWord("hello"));
        assertTrue(ex.getMessage().contains("только русские буквы"));
    }

    @Test
    @DisplayName("Выбрасывает BadWordLengthException при длине ≠ 5")
    void shouldThrowBadWordLengthExceptionOnWrongLength() {
        BadWordLengthException ex = assertThrows(BadWordLengthException.class, () -> game.tryToGuessWord("мама"));
        assertTrue(ex.getMessage().contains("из 5 букв"));
    }

    @Test
    @DisplayName("Выбрасывает WordNotFoundException при слове не в словаре")
    void shouldThrowWordNotFoundExceptionOnUnknownWord() {
        WordNotFoundException ex = assertThrows(WordNotFoundException.class, () -> game.tryToGuessWord("транг"));
        assertTrue(ex.getMessage().contains("не найдено"));
    }

    @Test
    @DisplayName("Игрок побеждает при угадывании слова")
    void shouldWinOnCorrectGuess() throws Exception {
        String result = game.tryToGuessWord("мечта");
        assertEquals("Вы выйграли!", result);
        assertFalse(game.isPlaying());
    }

    @Test
    @DisplayName("processKey: полное совпадение → +++++")
    void processKeyShouldBeCorrectForExactMatch() {
        assertEquals("+++++", game.processKey("мечта", "мечта"));
    }

    @Test
    @DisplayName("processKey: буква есть, но не на позиции → ^")
    void processKeyShouldMarkPresentButWrongPosition() {
        assertEquals("^-+-+", game.processKey("тучка", "мечта"));
    }

    @Test
    @DisplayName("processKey: учитывает частоту букв (не даёт ^ больше, чем нужно)")
    void processKeyShouldRespectLetterFrequency() {
        String answer = "мамаа";
        String guess = "мааам";
        String clue = new WordleGame(loader, sysWriter) {{
            answer = "мамаа";
        }}.processKey(guess, answer);

        assertEquals(5, clue.length());
        long plus = clue.chars().filter(ch -> ch == '+').count();
        long caret = clue.chars().filter(ch -> ch == '^').count();
        assertTrue(plus + caret <= 5);
    }

    @Test
    @DisplayName("getSuggestedWord выбрасывает DictionarySizeException при пустом словаре")
    void getSuggestedWordShouldThrowWhenNoWordsLeft() {
        game = new WordleGame(loader, sysWriter) {
            {
                answer = "мечта";
                availableWords.clear();
            }
        };

        DictionarySizeException ex = assertThrows(DictionarySizeException.class, () -> game.getSuggestedWord("бочка", "-----"));
        assertEquals("Словарь пуст", ex.getMessage());
    }
}