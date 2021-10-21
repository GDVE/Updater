package ru.simplemc.updater.executor;

import ru.simplemc.updater.Updater;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.pane.PaneTextStatus;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LauncherExecutor {

    private final String runtimeExecutableFilePath;
    private final String launcherExecutableFilePath;

    public LauncherExecutor(Path runtimeExecutableFilePath, Path launcherExecutableFilePath) {
        Updater.getFrame().setPane(new PaneTextStatus("Все обновлено", "Запуск лаунчера..."));
        this.runtimeExecutableFilePath = runtimeExecutableFilePath.toString();
        this.launcherExecutableFilePath = launcherExecutableFilePath.toString();
    }

    public void execute() throws IOException {

        List<String> params = new ArrayList<>();
        params.add(runtimeExecutableFilePath);
        params.add("-Dlaunched=true");
        if (OSUtils.isMacOS()) params.add("-Xdock:name=SimpleMinecraft.Ru - Launcher");
        params.add("-jar");
        params.add(launcherExecutableFilePath);

        waitForProcessStart(ProgramUtils.createNewProcess(params));
    }

    /**
     * Наблюдаем за процессом лаунчера, ждем от него заветного слова и выключаемся.
     *
     * @param process - запущенный процесс лаунчера
     */
    private void waitForProcessStart(Process process) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            long startingTime = System.currentTimeMillis();
            while ((line = reader.readLine()) != null && !line.contains("Launcher is started")) {
                if (startingTime >= System.currentTimeMillis() + 30000) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
