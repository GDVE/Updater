package ru.simplemc.updater;

import lombok.Getter;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.service.http.HttpService;
import ru.simplemc.updater.service.logger.SimpleLogger;
import ru.simplemc.updater.thread.UpdateThread;
import ru.simplemc.updater.utils.ProgramUtils;

public class Updater {

    @Getter
    private static Frame frame;
    @Getter
    private static UpdateThread thread;
    @Getter
    private static HttpService httpService;
    @Getter
    private static final SimpleLogger logger = new SimpleLogger(Updater.class);

    public static void main(String... args) {

        logger.info("Updater is started!");
        logger.info("Version: " + Environment.VERSION);
        logger.info("Preparing system environments...");

        ProgramUtils.prepareSystemEnv();
        logger.info("Preparing Frame...");
        frame = new Frame();
        frame.setStatus("Приветствую!", "Подготовка...");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        logger.info("Preparing HttpService...");
        httpService = new HttpService();

        logger.info("Preparing UpdaterThread...");
        thread = new UpdateThread();
        thread.start();
        logger.info("UpdaterThread started!");
    }
}
