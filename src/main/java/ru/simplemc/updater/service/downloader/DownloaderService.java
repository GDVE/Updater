package ru.simplemc.updater.service.downloader;

import org.apache.commons.io.IOUtils;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.gui.pane.DownloaderPane;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;
import ru.simplemc.updater.service.downloader.files.LauncherRuntime;
import ru.simplemc.updater.service.http.HttpServiceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class DownloaderService {

    private final DownloaderFile downloaderFile;
    private final DownloaderPane downloaderPane;
    private final ProgressBar progressBar;

    public DownloaderService(Frame frame, DownloaderFile downloaderFile) {
        this.downloaderFile = downloaderFile;
        this.downloaderPane = new DownloaderPane();
        this.progressBar = this.downloaderPane.getProgressBar();
        this.downloaderPane.setStatusAndDescription("Загрузка файлов", getPrettyFileName());
        frame.setPane(this.downloaderPane);
    }

    private String getPrettyFileName() {

        String fileName = this.downloaderFile.getPath().getFileName().toString();

        if (fileName.startsWith("Launcher.")) {
            return "Обновления лаунчера";
        }

        if (fileName.startsWith("SimpleMinecraft.")) {
            return "Обновления программы";
        }

        if (fileName.startsWith("jre-")) {
            return "Обновления Java";
        }

        return fileName;
    }

    /**
     * Запускает процесс загрузки файла
     *
     * @throws IOException - в случае какой либо неудачи
     */
    public void process() throws Exception {

        progressBar.setVisible(true);
        downloaderFile.prepareBeforeDownload();

        HttpURLConnection connection = HttpServiceManager.createConnection(downloaderFile.getUrl());
        connection.setRequestMethod("GET");
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(downloaderFile.getPath().toFile());

            byte[] buffer = new byte[4096];
            long currentFileSize = 0;
            int bufferSize;

            while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                currentFileSize += bufferSize;
                outputStream.write(buffer, 0, bufferSize);
                progressBar.setValue((int) (currentFileSize * 100F / downloaderFile.getSize()));
            }

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            progressBar.setVisible(false);
        }

        if (downloaderFile instanceof LauncherRuntime) {
            downloaderPane.setStatusAndDescription("Распаковка архива",
                    downloaderFile.getPath().getFileName().toString());
        }

        downloaderFile.prepareAfterDownload();
    }
}
