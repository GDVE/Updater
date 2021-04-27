package ru.simplemc.updater;

import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.pane.WelcomePane;
import ru.simplemc.updater.thread.UpdateThread;
import ru.simplemc.updater.utils.ProgramUtils;

public class Updater {

    public static void main(String... args) {

        ProgramUtils.prepareSystemEnv();

        Frame frame = new Frame();
        frame.setPane(new WelcomePane());

        Thread updaterThread = new UpdateThread(frame);
        updaterThread.start();
    }

}
