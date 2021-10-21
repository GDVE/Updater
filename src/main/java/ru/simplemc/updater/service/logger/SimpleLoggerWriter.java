package ru.simplemc.updater.service.logger;

import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleLoggerWriter {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Path path;

    public SimpleLoggerWriter(String fileName) {
        path = Paths.get(ProgramUtils.getStoragePath() + "/logs/" + fileName + ".log");
        if (Files.exists(path)) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfLogValid() {

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void writeToLog(String message) {
        executorService.execute(() -> {

            System.out.println(message);

            if (checkIfLogValid()) {

                List<String> lines;
                try {
                    lines = Files.readAllLines(path);
                } catch (IOException ignored) {
                    return;
                }

                lines.add(message);

                try {
                    Files.write(path, lines);
                } catch (IOException ignored) {
                }
            }
        });
    }
}
