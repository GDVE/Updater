package ru.simplemc.updater.executor;

import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.pane.StartupPane;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class UpdaterExecutor implements ApplicationExecutor {

    private final Path programPath;
    private final Frame frame;

    public UpdaterExecutor(Frame frame) throws IOException {

        this.frame = frame;
        this.programPath = ProgramUtils.getProgramPath();

        if (this.programPath == null)
            throw new IOException("Не удалось найти исполняемый файл программы!");
    }

    @Override
    public final void execute() throws IOException {

        List<String> params = new ArrayList<>();
        params.add("java");
        params.add("-jar");
        params.add(this.programPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(params);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(ProgramUtils.getStoragePath().toFile());
        processBuilder.start();

        ProgramUtils.haltProgram();
    }

    /**
     * Необходимо для корректной отрисовки надписей статуса загрузки
     */
    public void repaintFrame() {
        StartupPane startupPane = new StartupPane();
        startupPane.setStatusAndDescription("Обновление завершено", "Перезапуск программы");
        frame.setPane(startupPane);
    }

    /**
     * Небольшой хак позволяющий перезапускать программу после перезаписи
     * и избегать java.lang.NoClassDefFoundError
     *
     * @param frame - окошко программы
     * @return - возвращает UpdateExecutor для перезапуска программы после обновленя
     */
    public static UpdaterExecutor init(Frame frame) {
        if (!ProgramUtils.isDebugMode()) {
            try {
                return new UpdaterExecutor(frame);
            } catch (IOException e) {
                MessageUtils.printFullStackTraceWithExit("Не удалось создать сервис запуска программы!", e);
            }
        }

        return null;
    }
}
