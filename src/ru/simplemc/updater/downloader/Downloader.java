package ru.simplemc.updater.downloader;

import ru.simplemc.updater.downloader.file.DownloaderRuntimeArchiveFile;
import ru.simplemc.updater.downloader.file.DownloaderFile;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.gui.pane.DownloaderPane;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Downloader {

    private final DownloaderFile downloaderFile;
    private final DownloaderPane downloaderPane;
    private final ProgressBar progressBar;

    public Downloader(Frame frame, DownloaderFile downloaderFile) {
        this.downloaderFile = downloaderFile;
        this.downloaderPane = new DownloaderPane();
        this.progressBar = this.downloaderPane.getProgressBar();
        this.downloaderPane.setStatusAndDescription("Загрузка файла", this.downloaderFile.getPath().getFileName().toString());
        frame.setPane(this.downloaderPane);
    }

    public void process() throws IOException {

        this.progressBar.setVisible(true);
        this.downloaderFile.prepareBeforeDownload();

        InputStream inputStream = new BufferedInputStream(new URL(downloaderFile.getUrl()).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(downloaderFile.getPath().toFile());

        byte[] buffer = new byte[4096];
        int writeBytes = 0, bufferSize;

        while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) >= 0) {
            fileOutputStream.write(buffer, 0, bufferSize);
            this.progressBar.setValue((int) ((writeBytes += bufferSize) * 100 / downloaderFile.getSize()));
        }

        inputStream.close();
        fileOutputStream.close();

        this.progressBar.setVisible(false);

        if (downloaderFile instanceof DownloaderRuntimeArchiveFile) {
            downloaderPane.setStatusAndDescription("Распаковка архива", downloaderFile.getPath().getFileName().toString());
            ((DownloaderRuntimeArchiveFile) downloaderFile).unpack();
        }
    }
}
