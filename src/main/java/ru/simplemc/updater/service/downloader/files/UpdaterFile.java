package ru.simplemc.updater.service.downloader.files;

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

        appMoverPath = Paths.get(ProgramUtils.getStoragePath() + "/libs/AppMover-1.0.1.jar");
        Files.createDirectories(appMoverPath.getParent());

        try (InputStream inputStream = UpdaterFile.class.getResourceAsStream("/assets/mover/AppMover-1.0.1.app");
             OutputStream outputStream = new FileOutputStream(Paths.get(ProgramUtils.getStoragePath() +
                     "/libs/AppMover-1.0.1.jar").toFile())) {

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

    @Override
    public void prepareAfterDownload() throws IOException {

        MessageUtils.printSuccess("Обновление программы загружено!",
                "Сейчас запустится процесс обновления программы." +
                        "\nЕсли в течении 5 - 10 секунд ничего не произойдет, " +
                        "\nпопробуйте запустить программу снова или перекачайте ее с нашего сайта." +
                        "\n\nЖелаем приятной игры!");

        List<String> params = new ArrayList<>();
        params.add("java");
        params.add("-jar");
        params.add(appMoverPath.toString());
        params.add(this.getPath().toString());
        params.add(Objects.requireNonNull(ProgramUtils.getProgramPath()).toString());

        ProgramUtils.createNewProcess(params);
        ProgramUtils.haltProgram();
    }
}
