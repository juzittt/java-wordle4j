package ru.yandex.practicum;

import ru.yandex.practicum.exception.system.DictionarySizeException;
import ru.yandex.practicum.exception.validate.BadWordLengthException;
import ru.yandex.practicum.exception.validate.WordNotFoundException;
import ru.yandex.practicum.exception.validate.WrongLanguageException;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;
    protected String answer;
    private boolean isWon = false;
    private int steps = 0;
    private int attemptsLeft = MAX_STEPS;
    private final WordleDictionary dictionary;
    protected final List<String> availableWords;
    private final Random random;
    private final PrintWriter sysLog;
    private final LocalDateTime dateTime;
    private final List<Guess> guesses = new ArrayList<>();
    private String word;
    private String key;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public WordleGame(WordleDictionaryLoader loader, PrintWriter sysLog) {
        WordleDictionary fullDict = loader.wordsFileLoader(loader.getFilename());
        this.dictionary = fullDict;
        this.availableWords = new ArrayList<>(fullDict.getDictionaryList());
        this.answer = dictionary.makeWord();
        this.random = new Random();
        this.sysLog = sysLog;
        this.dateTime = LocalDateTime.now();
    }

    public boolean isPlaying() {
        return !isWon;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public int getSteps() {
        return steps;
    }

    public String getWord() {
        return word;
    }

    public String getKey() {
        return key;
    }

    public String tryToGuessWord(String word) throws WordNotFoundException, BadWordLengthException, WrongLanguageException {

        validateWordLanguage(word);
        validateWordLength(word);
        validateWordInDictionary(word);

        this.word = word;

        if (word.equals(answer)) {
            isWon = true;
            return "Вы выйграли!";
        }
        steps++;
        attemptsLeft = MAX_STEPS - steps;
        key = processKey(word, answer);
        guesses.add(new Guess(word, key));

        if (attemptsLeft == 0) {
            return "Вы проиграли... загаданное слово - " + answer;
        }
        return key;
    }

    private void validateWordLanguage(String word) throws WrongLanguageException {
        if (!word.matches("[а-яА-Я-]+")) {
            throw new WrongLanguageException("WrongLanguageException: только русские буквы: " + word);
        }
    }

    private void validateWordLength(String word) throws BadWordLengthException {
        if (word.length() != 5) {
            throw new BadWordLengthException("BadWordLengthException: слово должно быть из 5 букв: " + word);
        }
    }

    private void validateWordInDictionary(String word) throws WordNotFoundException {
        if (!dictionary.contains(word)) {
            throw new WordNotFoundException("WordNotFoundException: слово не найдено: " + word);
        }
    }

    public String processKey(String word, String answer) {
        StringBuilder builder = new StringBuilder(WORD_LENGTH);

        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessChar = word.charAt(i);
            char answerChar = answer.charAt(i);

            if (guessChar == answerChar) {
                builder.append("+");
            } else if (answer.indexOf(guessChar) >= 0) {
                builder.append("^");
            } else {
                builder.append("-");
            }
        }
        return builder.toString();
    }

    public String getSuggestedWord(String word, String key) {
        if (word == null || key == null || guesses.isEmpty()) {
            if (availableWords.isEmpty()) {
                throw new DictionarySizeException("Словарь пуст");
            }
            return availableWords.get(random.nextInt(availableWords.size()));
        }

        List<String> filtered = new ArrayList<>(availableWords);
        for (Guess g : guesses) {
            filtered = filterWords(filtered, g.word, g.key);
        }

        if (filtered.isEmpty()) {
            sysLog.println(dateTime.format(DATE_TIME_FORMATTER) + " - Нет подходящих слов для подсказки.");
            return "Подсказка недоступна";
        }

        return filtered.get(random.nextInt(filtered.size()));
    }

    private List<String> filterWords(List<String> words, String guess, String key) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (matchesKey(word, guess, key)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean matchesKey(String candidate, String guess, String key) {
        return processKey(guess, candidate).equals(key);
    }

    private class Guess {
        final String word;
        final String key;

        Guess(String word, String key) {
            this.word = word;
            this.key = key;
        }
    }
}
