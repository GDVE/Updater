package ru.simplemc.updater.service.downloader.files;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;
import ru.simplemc.updater.service.downloader.beans.FileInfo;
import ru.simplemc.updater.utils.CryptUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdaterFile extends DownloaderFile {

    private final Path appMoverPath;

    public UpdaterFile(FileInfo fileInfo) {
        super(fileInfo);
        this.appMoverPath = Paths.get(ProgramUtils.getStoragePath() + "/runtime/AppMover-"
                + Environment.APP_MOVER_VERSION + ".jar");
    }

    @Override
    public boolean isInvalid() {
        return !ProgramUtils.isDebugMode()
                && !getMd5().equals(CryptUtils.md5(Objects.requireNonNull(ProgramUtils.getProgramPath())));
    }

    @Override
    public void prepareBeforeDownload() throws IOException {
        super.prepareBeforeDownload();

        if (!Files.exists(appMoverPath)) {

            Files.createDirectories(appMoverPath.getParent());

            try (InputStream inputStream = getAppMoverResource();
                 OutputStream outputStream = new FileOutputStream(appMoverPath.toFile())) {

                if (inputStream == null) {
                    throw new IOException("AppMover is not present!");
                }

                byte[] buffer = new byte[1024];
                int bufferSize;

                while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                    outputStream.write(buffer, 0, bufferSize);
                }
            }
        }
    }

    private InputStream getAppMoverResource() {
        return getClass().getResourceAsStream("/assets/mover/" + appMoverPath.getFileName().toString()
                .replace(".jar", ".app"));
    }

    @Override
    public void prepareAfterDownload() throws IOException {

        List<String> params = new ArrayList<>();
        params.add("java");
        params.add("-jar");
        params.add(appMoverPath.toString());
        params.add(path.toString());
        params.add(Objects.requireNonNull(ProgramUtils.getProgramPath()).toString());

        try {
            Updater.getFrame().setStatus("Обновление", "Перезапуск программы...");
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        ProgramUtils.createNewProcess(params);
        ProgramUtils.haltProgram();
    }
}
