package ru.simplemc.updater.utils;

import java.io.File;
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
     * Рекурсивно удаляет папки и файлы и вложенные в них файлы и папки
     *
     * @param file - директория, которую нужно удалить
     */
    public static void deleteFilesRecursive(File file) {
        deleteFilesRecursive(file.toPath());
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
            System.out.println("Неудалось удалить файл: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Удаляет указанный файл
     *
     * @param file - файл, который нужно удалить
     */
    public static void deleteFile(File file) {
        deleteFile(file.toPath());
    }

    /**
     * @param kilobytes - размер файла в килобайтах
     * @return - возвращает размер понятный для человека
     */
    public static String transformToHumanSize(int kilobytes) {

        if (kilobytes > 1024 * 1024) {
            return kilobytes / 1024 * 1024 + " Гб";
        }

        if (kilobytes > 1024) {
            return kilobytes / 1024 + " Мб";
        }

        return kilobytes + " Кб";
    }
}
