package ru.simplemc.updater.service.downloader.files;

import org.apache.commons.io.IOUtils;
import ru.simplemc.updater.gui.utils.MessageUtils;
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

    private Path appMoverPath;

    public UpdaterFile(FileInfo fileInfo) {
        super(fileInfo);
    }

    @Override
    public boolean isInvalid() {
        return !ProgramUtils.isDebugMode()
                && !getMd5().equals(CryptUtils.md5(Objects.requireNonNull(ProgramUtils.getProgramPath())));
    }

    @Override
    public void prepareBeforeDownload() throws IOException {
        super.prepareBeforeDownload();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        appMoverPath = Paths.get(ProgramUtils.getStoragePath() + "/libs/AppMover-1.0.1.jar");
        Files.createDirectories(appMoverPath.getParent());

        try {
            inputStream = UpdaterFile.class.getResourceAsStream("/assets/mover/AppMover-1.0.1.app");
            outputStream = new FileOutputStream(Paths.get(ProgramUtils.getStoragePath() +
                    "/libs/AppMover-1.0.1.jar").toFile());

            byte[] buffer = new byte[4096];
            int bufferSize;

            while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, bufferSize);
            }

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public void prepareAfterDownload() throws IOException {

        MessageUtils.printSuccess("Обновление программы загружено!", "Сейчас запустится процесс обновления " +
                "программы.\nЕсли в течении 5 - 10 секунд ничего не произойдет, то попробуйте запустить программу снова, " +
                "\nлибо во время обновления возникнут ошибки, то просто скачайте лаунчер с сайта, там всегда доступна " +
                "самая новая версия!" +
                "\n\nМы работаем для вас! Желаем приятной игры!");

        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add(appMoverPath.toString());
        args.add(this.getPath().toString());
        args.add(Objects.requireNonNull(ProgramUtils.getProgramPath()).toString());

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.start();

        ProgramUtils.haltProgram();
    }
}
