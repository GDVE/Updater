package ru.simplemc.updater.service.downloader;

import org.apache.commons.io.IOUtils;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.gui.pane.PaneDownloader;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;
import ru.simplemc.updater.service.downloader.files.LauncherRuntime;
import ru.simplemc.updater.service.http.HttpServiceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class DownloaderService {

    private final DownloaderFile file;
    private final PaneDownloader downloaderPane;

    public DownloaderService(DownloaderFile file) {
        this.file = file;
        Updater.getFrame().setPane(downloaderPane = new PaneDownloader());
    }

    private String getPrettyFileName() {

        String fileName = this.file.getPath().getFileName().toString();

        if (fileName.startsWith("Launcher.")) {
            return "Скачивание лаунчера...";
        }

        if (fileName.startsWith("SimpleMinecraft.")) {
            return "Скачивание новой версии...";
        }

        if (fileName.startsWith("jre-")) {
            return "Скачивание Java Runtime...";
        }

        return fileName;
    }

    /**
     * Запускает процесс загрузки файла
     *
     * @throws IOException - в случае какой либо неудачи
     */
    public void process() throws IOException {

        downloaderPane.getProgressBar().setVisible(true);
        downloaderPane.setCurrentStatus("Обновление", getPrettyFileName());
        file.prepareBeforeDownload();

        HttpURLConnection connection = HttpServiceManager.createConnection(file.getUrl());
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(file.getPath().toFile());

            byte[] buffer = new byte[4096];
            long currentFileSize = 0;
            int bufferSize;

            while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                currentFileSize += bufferSize;
                outputStream.write(buffer, 0, bufferSize);
                downloaderPane.getProgressBar().setValue((int) (currentFileSize * 100F / file.getSize()));
            }

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            downloaderPane.getProgressBar().setVisible(false);
        }

        if (file instanceof LauncherRuntime) {
            downloaderPane.setCurrentStatus("Распаковка архива", getFileName(file));
        }

        downloaderPane.setCurrentStatus("Обновление", "Загружен " + getFileName(file));
        file.prepareAfterDownload();
    }

    private static String getFileName(DownloaderFile file) {
        return file.getPath().getFileName().toString();
    }
}
