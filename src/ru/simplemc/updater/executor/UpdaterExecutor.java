package ru.simplemc.updater.executor;

import ru.simplemc.updater.Updater;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UpdaterExecutor implements ApplicationExecutor {

    private final Path programPath;

    public UpdaterExecutor() throws IOException {

        this.programPath = ProgramUtils.getProgramPath();

        if (this.programPath == null)
            throw new IOException("Не удалось найти исполняемый файл программы!");
    }

    @Override
    public void execute() throws IOException {

        List<String> params = new ArrayList<>();

        params.add("java");
        params.add("-jar");
        params.add(this.programPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(params);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(ProgramUtils.getStoragePath().toFile());
        processBuilder.start();
    }
}
