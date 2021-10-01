package ru.simplemc.updater.utils;

import oshi.SystemInfo;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class OSUtils {

    private final static int OS_ID = genOperationSystemId();
    private final static int OS_ARCH = isWindows()
            ? (System.getenv("ProgramFiles(x86)") == null ? 32 : 64)
            : (System.getProperty("os.arch").contains("64") ? 64 : 32);

    /**
     * @return Возвращает ID операционной системы
     */
    private static int genOperationSystemId() {

        String operationSystem = System.getProperty("os.name").toLowerCase();

        if (operationSystem.contains("mac"))
            return 2;

        if (operationSystem.contains("win"))
            return 1;

        if (operationSystem.contains("linux") || operationSystem.contains("unix"))
            return 0;

        return -1;
    }

    /**
     * @return Возвращает "красивое" имя операционной системы с учетом регистра
     */
    public static String getSystemIdWithArch() {

        switch (OS_ID) {
            case 0:
                return isX64() ? "linux-x64" : "linux-i586";
            case 1:
                return isX64() ? "windows-x64" : "windows-i586";
            case 2:
                return getProcessorName().contains("M1") ? "macos-aarch64" : "macos-x64";
        }

        return isX64() ? "unknown-x64" : "unknown-i586";
    }

    /**
     * @return Возвращает true если система 64-х разрядная
     */
    public static boolean isX64() {
        return OS_ARCH == 64;
    }

    /**
     * @return Возвращает true при работе на MacOS системах
     */
    public static boolean isMacOS() {
        return OS_ID == 2;
    }

    /**
     * @return Возвращает true при работе на Windows системах
     */
    public static boolean isWindows() {
        return OS_ID == 1;
    }

    /**
     * @return Возвращает true при работе на Unix/Linux системах
     */
    public static boolean isLinux() {
        return OS_ID == 0;
    }

    /**
     * Копирует произвольный текст в буфер обмена системы
     *
     * @param str - произвольная строка для копирования в буфер обмена
     */
    public static void copyToClipboard(String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str), null);
    }

    /**
     * Открывает в браузере по умолчанию URL адрес
     *
     * @param URL - ссылка необходимая для открытия в системном браузере
     */
    public static void openLinkInSystemBrowser(String URL) {

        if (isLinux()) {
            new Thread(() -> {

                try {
                    Desktop.getDesktop().browse(new URL(URL).toURI());
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }

            }).start();
        } else
            try {
                Desktop.getDesktop().browse(new URL(URL).toURI());
            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
    }

    public static String getProcessorName() {
        return new SystemInfo().getHardware().getProcessor().getProcessorIdentifier().getName();
    }
}
