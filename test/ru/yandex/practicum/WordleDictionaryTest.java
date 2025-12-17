package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dictionary")
@DisplayName("Тесты WordleDictionary — хранение и нормализация слов")
public class WordleDictionaryTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary();
    }

    @Test
    @DisplayName("Должен нормализовать ё в е и добавить слово")
    void shouldNormalizeAndAddWordWithYo() {
        dictionary.addWord("Ёлка");
        assertTrue(dictionary.contains("елка"));
        List<String> words = dictionary.getDictionaryList();
        assertEquals(1, words.size());
        assertEquals("елка", words.get(0));
    }

    @Test
    @DisplayName("Не должен добавлять дубликаты слов")
    void shouldNotAddDuplicateWords() {
        dictionary.addWord("Мечта");
        dictionary.addWord("мечта");
        assertEquals(1, dictionary.size());
    }

    @Test
    @DisplayName("Слово должно находиться после добавления")
    void shouldContainAddedWord() {
        dictionary.addWord("домик");
        assertTrue(dictionary.contains("домик"));
    }

    @Test
    @DisplayName("makeWord возвращает одно из добавленных слов")
    void makeWordReturnsOneOfAddedWords() {
        dictionary.addWord("дом");
        dictionary.addWord("лес");
        dictionary.addWord("река");

        String word = dictionary.makeWord();
        assertTrue(List.of("дом", "лес", "река").contains(word));
    }
}