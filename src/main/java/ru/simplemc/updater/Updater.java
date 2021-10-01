package ru.simplemc.updater;

import lombok.Getter;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.thread.UpdateThread;
import ru.simplemc.updater.utils.ProgramUtils;

public class Updater {

    @Getter
    private static Frame frame;
    @Getter
    private static UpdateThread thread;

    public static void main(String... args) {

        ProgramUtils.prepareSystemEnv();

        frame = new Frame();
        frame.setStatus("Приветствую!", "Подготовка...");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        thread = new UpdateThread();
        thread.start();
    }
}
