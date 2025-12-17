package ru.yandex.practicum;

import ru.yandex.practicum.exception.system.FileNotFound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private static final String FILENAME = "words_ru.txt";
    private static final int WORD_LENGTH = 5;
    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
    private final PrintWriter sysLog;
    private final LocalDateTime dateTime;

    public WordleDictionaryLoader(PrintWriter sysLog) {
        this.sysLog = sysLog;
        dateTime = LocalDateTime.now();
    }

    public WordleDictionary wordsFileLoader(String fileName) {
        WordleDictionary dictionary = new WordleDictionary();
        File file = new File(fileName);

        if (!file.exists() || !file.canRead()) {
            sysLog.println(dateTime.format(DATE_TIME_FORMATTER) + " - Файл не найден: " + fileName);
            throw new FileNotFound(new FileNotFoundException("Файл не найден: " + fileName));
        }

        try (var br = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == WORD_LENGTH) {
                    dictionary.addWord(line);
                }
            }
        } catch (FileNotFoundException e) {
            sysLog.println(dateTime.format(DATE_TIME_FORMATTER) + " - " + e);
            throw new FileNotFound(e);
        } catch (IOException e) {
            sysLog.println(dateTime.format(DATE_TIME_FORMATTER) + " - " + e);
            throw new RuntimeException(e);
        }
        return dictionary;
    }

    public String getFilename() {
        return FILENAME;
    }
}
