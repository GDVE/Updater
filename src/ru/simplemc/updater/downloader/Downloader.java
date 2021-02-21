package ru.simplemc.updater.downloader;

import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.gui.pane.DownloaderPane;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class Downloader {

    private final Frame frame;
    private final DownloaderFile downloaderFile;
    private final DownloaderPane downloaderPane;
    private final ProgressBar progressBar;

    public Downloader(Frame frame, DownloaderFile downloaderFile) {
        this.frame = frame;
        this.downloaderFile = downloaderFile;
        this.downloaderPane = new DownloaderPane();
        this.progressBar = this.downloaderPane.getProgressBar();
        this.frame.setPane(this.downloaderPane);
    }

    public void process() throws IOException {

        this.progressBar.setVisible(true);

        if (!Files.exists(downloaderFile.getPath())) {
            Files.createDirectories(downloaderFile.getPath());
        } else {
            Files.deleteIfExists(downloaderFile.getPath());
        }

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
    }
}
