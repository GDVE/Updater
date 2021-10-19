package ru.simplemc.updater.service.downloader;

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
import java.nio.file.Files;

public class DownloadingProcess {

    private final DownloaderFile file;
    private final PaneDownloader downloaderPane;

    public DownloadingProcess(DownloaderFile file) {
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
            return "Скачивание игровой версии Java...";
        }

        return fileName;
    }

    /**
     * Запускает процесс загрузки файла
     *
     * @throws IOException - в случае какой либо неудачи
     */
    public void run() throws IOException {

        downloaderPane.getProgressBar().setVisible(true);
        downloaderPane.setCurrentStatus("Обновление", getPrettyFileName());
        file.prepareBeforeDownload();

        try {
            downloadFile(false);
        } catch (DownloadNotCompleteException e) {

            int errorsCounter = 0;

            while (true) {
                System.out.println("Retry downloading file: " + file.getUrl());

                try {
                    downloadFile(true);
                    break;
                } catch (DownloadNotCompleteException ignored) {
                    errorsCounter++;
                    if (errorsCounter == 10) {
                        throw new IOException("Не удалось докачать поврежденный файл: " + file.getName());
                    }
                }
            }
        }

        downloaderPane.getProgressBar().setVisible(false);

        if (file instanceof LauncherRuntime) {
            downloaderPane.setCurrentStatus("Распаковка архива", getFileName(file));
        }

        file.prepareAfterDownload();
    }

    private void downloadFile(boolean canSkip) throws IOException, DownloadNotCompleteException {

        HttpURLConnection connection = HttpServiceManager.createConnection(file.getUrl());

        if (canSkip) {
            connection.setRequestProperty("Range", "bytes=" + Files.size(file.getPath()) + "-" + file.getSize());
        }

        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(file.getPath().toFile())) {

            byte[] dataBuffer = new byte[1024];
            long bytesWritten = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                outputStream.write(dataBuffer, 0, bytesRead);
                bytesWritten += bytesRead;
                downloaderPane.getProgressBar().setValue((int) (bytesWritten * 100F / file.getSize()));
            }
        } catch (IOException e) {
            downloaderPane.getProgressBar().setVisible(false);
            throw e;
        }

        if (!Files.exists(file.getPath()) || Files.size(file.getPath()) < file.getSize()) {
            throw new DownloadNotCompleteException();
        }

        downloaderPane.setCurrentStatus("Обновление", "Загружен " + getFileName(file));
    }

    private static String getFileName(DownloaderFile file) {
        return file.getPath().getFileName().toString();
    }
}
