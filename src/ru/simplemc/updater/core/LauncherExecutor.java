package ru.simplemc.updater.core;

import ru.simplemc.updater.core.updaters.RuntimeUpdater;
import ru.simplemc.updater.ui.Frame;
import ru.simplemc.updater.ui.pane.StartupPane;
import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.SystemUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LauncherExecutor {

    private final Frame frame;
    private final File file;
    private final RuntimeUpdater runtimeUpdater;
    private final RuntimeInstaller runtimeInstaller;

    public LauncherExecutor(Frame frame, File file, RuntimeUpdater runtimeUpdater) {
        this.frame = frame;
        this.file = file;
        this.runtimeUpdater = runtimeUpdater;
        this.runtimeInstaller = runtimeUpdater.getInstaller();
    }

    public void execute() {

        StartupPane startupPane = new StartupPane();
        frame.setPane(startupPane);

        String runtimeExecutablePath = runtimeInstaller.getRuntimeExecutableFile().getAbsolutePath();
        List<String> params = new ArrayList<>();

        params.add(runtimeExecutablePath);
        params.add("-jar");
        params.add(file.getPath());

        Process launcherProcess = null;

        try {
            launcherProcess = new ProcessBuilder(params).start();
        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("Try start with Runtime.exec()...");

            try {
                launcherProcess = Runtime.getRuntime().exec(runtimeExecutablePath + " -jar " + file.getPath());
            } catch (IOException ex) {
                MessageUtil.printException("Произошла ошибка при запуске лаунчера!", "После нажатия OK, запустится процесс исправления ошибок.\nЕсли ошибка появляется повторно, сообщите об этом администрации (https://vk.com/goodvise)\n", e, false);
                runtimeInstaller.uninstall();
                runtimeUpdater.checkForUpdate();
            }
        }

        startupPane.setStatusAndDescription("ЛАУНЧЕР ЗАПУСКАЕТСЯ", "Удачной игры на серверах!");

        if (launcherProcess != null) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(launcherProcess.getInputStream()));
            String line;

            try {

                while ((line = reader.readLine()) != null)
                    if (line.contains("Launcher is started"))
                        break;

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        SystemUtil.halt();
    }

}
