package ru.simplemc.updater.utils;

import ru.simplemc.updater.Updater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FilesUtils {

    /**
     * Рекурсивно удаляет папки и файлы и вложенные в них файлы и папки
     *
     * @param path - путь до директории к удалению
     */
    public static void deleteFilesRecursive(Path path) {
        if (Files.exists(path))
            try {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(FilesUtils::deleteFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Удаляет указанный файл
     *
     * @param path - путь до файла, который нужно удалить
     */
    public static void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            Updater.getLogger().error("Failed to delete file: " + path, e);
        }
    }
}
