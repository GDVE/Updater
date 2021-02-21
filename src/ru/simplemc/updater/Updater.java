package ru.simplemc.updater;

import ru.simplemc.updater.core.MainThread;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.pane.WelcomePane;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Updater {

    public static void main(String... args) {

        prepareSystemEnv();

        Frame frame = new Frame(Settings.FRAME_TITLE);
        frame.setPane(new WelcomePane());

        Thread updaterThread = new Thread(new MainThread(frame));
        updaterThread.start();
    }

    /**
     * Подготовка системых параметров для работы программы
     */
    private static void prepareSystemEnv() {

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

}
