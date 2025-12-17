package ru.yandex.practicum;

import ru.yandex.practicum.exception.validate.BadWordLengthException;
import ru.yandex.practicum.exception.validate.WordNotFoundException;
import ru.yandex.practicum.exception.validate.WrongLanguageException;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
    private final WordleGame game;
    private final Scanner scanner;
    private final PrintWriter validationLog;
    private final LocalDateTime dateTime;

    public Wordle(PrintWriter sysWriter, PrintWriter valWriter, InputStream in) {
        validationLog = valWriter;
        var loader = new WordleDictionaryLoader(sysWriter);
        game = new WordleGame(loader, sysWriter);
        scanner = new Scanner(in);
        dateTime = LocalDateTime.now();
    }

    public Wordle(PrintWriter sysWriter, PrintWriter valWriter) {
        this(sysWriter, valWriter, System.in);
    }

    public static void main(String[] args) {
        try (var systemWriter = new PrintWriter(
                new FileWriter("system.log", true));
             var validationWriter = new PrintWriter(
                     new FileWriter("validation.log", true))) {
            new Wordle(systemWriter, validationWriter).startGame(false);
        } catch (Exception e) {
            System.err.println("Критическая ошибка - приложение закрыто.");
        }
    }

    protected void startGame(boolean isTesting) throws Exception {
        System.out.printf("%30s", "Отгадайте слово из 5 букв: ");

        while (game.isPlaying() && game.getSteps() < 6) {
            try {
                String guess = scanner.nextLine().trim().toLowerCase().replaceAll("ё", "е");
                guess = processEmptyInput(guess);
                processGame(guess);
                if (isTesting) {
                    break;
                }
            } catch (WordNotFoundException ex) {
                processException(isTesting, ex, "Не найдено. Попробуйте еще: ");
            } catch (BadWordLengthException ex) {
                processException(isTesting, ex, "Слово должно быть из 5 букв: ");
            } catch (WrongLanguageException ex) {
                processException(isTesting, ex, "Только русские буквы: ");
            }
        }
        scanner.close();
    }

    private String processEmptyInput(String guess) {
        if (guess.isEmpty()) {
            String hint = game.getSuggestedWord(game.getWord(), game.getKey());
            System.out.printf("%35s%n", "Подсказка: " + hint);
            return hint;
        }
        return guess;
    }

    private void processGame(String guess)
            throws WordNotFoundException, BadWordLengthException, WrongLanguageException {
        String gameMessage = game.tryToGuessWord(guess);
        if (game.getAttemptsLeft() > 0 && game.isPlaying()) {
            System.out.printf("%35s%n", gameMessage);
            System.out.printf("Число оставшихся попыток - %d: ", game.getAttemptsLeft());
        } else {
            System.out.print(gameMessage);
        }
    }

    private void processException(boolean isThrow, Exception e, String message) throws Exception {
        if (isThrow) {
            throw e;
        }
        System.out.printf("%30s", message);
        validationLog.printf("%s - %s%n", dateTime.format(DATE_TIME_FORMATTER), e.getMessage());
    }

}
