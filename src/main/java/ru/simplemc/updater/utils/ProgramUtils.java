package ru.simplemc.updater.utils;

import ru.simplemc.updater.Environment;
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
import java.util.List;

public class ProgramUtils {

    public static boolean isDebugMode() {
        return getProgramMD5Hash().equals("4f9b53500448cf766c81c7e68d614283");
    }

    public static String getProgramMD5Hash() {

        Path programPath = getProgramPath();

        if (programPath != null) {
            if (programPath.endsWith("classes/java/main"))
                return "4f9b53500448cf766c81c7e68d614283";
            else
                return CryptUtils.md5(programPath);
        }

        Updater.getLogger().error("Failed to get program hash!");
        MessageUtils.printErrorWithShutdown("Ошибка доступа к файлу лаунчера",
                "Не удалось определить контрольную сумму программы.");

        haltProgram();
        return "";
    }

    public static void prepareSystemEnv() {

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("http.agent", Environment.HTTP_USER_AGENT);

        try {
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Throwable ignored) {
        }
    }

    public static Path getProgramPath() {

        try {
            return Paths.get(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            Updater.getLogger().error("Failed to get program path:", e);
            MessageUtils.printErrorWithShutdown("",
                    "Не удалось определить расположение исполняемого файла лаунчера");
            haltProgram();
        }

        return null;
    }

    public static String getProgramExtension() {
        Path programPath = getProgramPath();
        return programPath != null && programPath.getFileName().toString().endsWith(".exe") ? "exe" : "jar";
    }

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
            } else Files.createDirectory(path);
        } catch (IOException e) {
            Updater.getLogger().error("Failed to create storage dir:", e);
            MessageUtils.printErrorWithShutdown("Произошла ошибка",
                    "Не удалось создать рабочую директорию лаунчера");
            haltProgram();
        }

        return path;
    }

    public static void haltProgram() {
        Updater.getLogger().info("Program is closed! Bye-bye!");
        System.exit(0);
    }

    public static Process createNewProcess(List<String> params) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
}
