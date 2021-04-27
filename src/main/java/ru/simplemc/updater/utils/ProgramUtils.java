package ru.simplemc.updater.utils;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.gui.utils.MessageUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProgramUtils {

    /**
     * @return - возвращает true если программа запущена в режиме разработки/отладки
     */
    public static boolean isDebugMode() {
        return getProgramMD5Hash().equals("4f9b53500448cf766c81c7e68d614283");
    }

    /**
     * @return Возвращает текущую контрольную сумму (MD5) программы.
     */
    public static String getProgramMD5Hash() {

        Path programPath = getProgramPath();

        if (programPath != null) {
            if (programPath.endsWith("classes/java/main"))
                return "4f9b53500448cf766c81c7e68d614283";
            else
                return CryptUtils.md5(programPath);
        }

        MessageUtils.printErrorWithShutdown("Ошибка доступа к файлу лаунчера", "Не удалось определить контрольную сумму лаунчера.");
        haltProgram();
        return "";
    }

    /**
     * Подготовка системых параметров для работы программы
     */
    public static void prepareSystemEnv() {

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("http.agent", Settings.HTTP_USER_AGENT);

        try {
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Throwable ignored) {
        }
    }

    /**
     * @return Возвращает полный путь до исполняемого файла программы.
     */
    public static Path getProgramPath() {

        try {
            return Paths.get(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось определить расположение исполняемого файла лаунчера", e);
            haltProgram();
        }

        return null;
    }

    public static String getProgramPathString() {
        return String.valueOf(getProgramPath());
    }

    /**
     * @return Возвращает полный путь до рабочей папки прграммы.
     */
    public static Path getStoragePath() {

        Path path;

        if (OSUtils.isWindows()) {

            String parentDirPath = System.getenv("AppData");

            if (parentDirPath == null)
                parentDirPath = System.getProperty("user.home", ".");

            path = Paths.get(parentDirPath + "/.simplemc");

            if (path.toString().contains("!")) {
                path = Paths.get(System.getenv("SystemDrive") + "/.simplemc");
            }

        } else {
            path = Paths.get(System.getProperty("user.home", ".") + "/simpleminecraft");
        }

        try {

            if (Files.exists(path)) {
                if (!Files.isDirectory(path)) Files.delete(path);
            } else
                Files.createDirectory(path);

        } catch (IOException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось создать рабочую директорию лаунчера", e);
            haltProgram();
        }

        return path;
    }

    /**
     * @return Возвращает текущее расширение исполняемого файла программы.
     */
    public static String getExecutableFileExtension() {

        Path programPath = getProgramPath();

        if (programPath != null && programPath.getFileName().toString().endsWith(".exe")) {
            return "exe";
        }

        return "jar";
    }

    /**
     * Так сказать полное заверешние процесса программы.
     */
    public static void haltProgram() {

        try {
            Class<?> af = Class.forName("java.lang.Shutdown");
            Method m = af.getDeclaredMethod("halt0", int.class);
            m.setAccessible(true);
            m.invoke(null, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * Полное завершение программы, но с сообщением в System.out.
     *
     * @param message - сообщение для вывода в System.out
     */
    public static void haltProgram(String message) {
        System.out.println(message);
        haltProgram();
    }

}
