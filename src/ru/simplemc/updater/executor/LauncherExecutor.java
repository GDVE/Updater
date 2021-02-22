package ru.simplemc.updater.executor;

import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.pane.StartupPane;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LauncherExecutor implements ApplicationExecutor {

    private final String runtimeExecutableFilePath;
    private final String launcherExecutableFilePath;

    public LauncherExecutor(Frame frame, String runtimeExecutableFilePath, String launcherExecutableFilePath) {
        this.runtimeExecutableFilePath = runtimeExecutableFilePath;
        this.launcherExecutableFilePath = launcherExecutableFilePath;
        frame.setPane(new StartupPane());
    }

    @Override
    public void execute() throws IOException {

        List<String> processPrams = new ArrayList<>();
        processPrams.add(runtimeExecutableFilePath);
        processPrams.add("-jar");
        processPrams.add(launcherExecutableFilePath);

        ProcessBuilder processBuilder = new ProcessBuilder(processPrams);
        spectateForProcess(processBuilder.start());
    }

    /**
     * Наблюдаем за процессом лаунчера, ждем от него заветного слова и выключаемся :)
     * @param process - запущенный процесс лаунчера
     */
    private void spectateForProcess(Process process) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        try {
            while ((line = reader.readLine()) != null)
                if (line.contains("Launcher is started")) {
                    break;
                }
        } catch (IOException ignored) {
        }

        ProgramUtils.haltProgram();
    }
}
