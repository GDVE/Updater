package ru.simplemc.updater.util;

import ru.simplemc.updater.Updater;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class PathUtil {

    public static String getExecutableFileExtension(File file) {
        return isFileExtensionCompare(file, "exe") ? "exe" : "jar";
    }

    public static boolean isFileExtensionCompare(File file, String format) {
        return file.getPath().endsWith(format);
    }

    public static boolean isExecutableFile(File file) {
        return PathUtil.isFileExtensionCompare(file, "exe") || PathUtil.isFileExtensionCompare(file, "jar");
    }

    public static File getUpdaterApplicationPath() {

        try {
            return new File(URLDecoder.decode(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "UTF-8"));
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            MessageUtil.printException("Неудалось определить место-положение приложения!", "Возможно, проблема связана с именем пользователя в вашей системе или неисправностью файловой системы.", e);
        }

        return null;
    }

    public static File getStorageDirectoryPath() {

        String userHome = java.lang.System.getProperty("user.home", ".");
        String storageDirectoryName = SystemUtil.isUnix() ? "simplemc" : ".simplemc";
        File directoryFile = new File(userHome, storageDirectoryName + File.separator);

        if (SystemUtil.isWindows()) {

            String appData = java.lang.System.getenv("AppData");

            if (appData != null)
                directoryFile = new File(appData, storageDirectoryName + File.separator);
            else
                directoryFile = new File(userHome, storageDirectoryName + File.separator);
        }

        if (SystemUtil.isMac())
            directoryFile = new File(userHome, "Library" + File.separator + "Application Support" + File.separator + storageDirectoryName + File.separator);

        if (directoryFile.toString().contains("!"))
            directoryFile = new File(java.lang.System.getenv("SystemDrive"), ".simplemc");

        if (!directoryFile.exists() && !directoryFile.mkdirs())
            MessageUtil.printError("Неудалось создать рабочую папку!", "Что-то помешало создать папку для загрузки компонентов лаунчера.\nПопробуйте отключить антивирусные программы.");

        return directoryFile;
    }

    public static void recursiveDelete(File file) {
        try {

            if (!file.exists()) return;
            if (file.isDirectory())
                for (File recursiveFile : file.listFiles())
                    recursiveDelete(recursiveFile);

            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
