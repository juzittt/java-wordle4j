package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final HashSet<String> words;
    private final List<String> wordsList;
    private final Random random;


    public WordleDictionary() {
        words = new HashSet<>();
        wordsList = new ArrayList<>();
        random = new Random();
    }

    public List<String> getDictionaryList() {
        return new ArrayList<>(wordsList);
    }

    public void addWord(String word) {
        String normalized = word.toLowerCase().replaceAll("ё", "е");
        if (words.add(normalized)) {
            wordsList.add(normalized);
        }
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String makeWord() {
        if (wordsList.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        return wordsList.get(random.nextInt(wordsList.size()));
    }

    public int size() {
        return wordsList.size();
    }
}
