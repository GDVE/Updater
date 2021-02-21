package ru.simplemc.updater.core;

import ru.simplemc.updater.data.json.JSONFile;
import ru.simplemc.updater.ui.Frame;
import ru.simplemc.updater.ui.ProgressBar;
import ru.simplemc.updater.ui.pane.DownloaderPane;
import ru.simplemc.updater.util.MessageUtil;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Downloader {

    private final String url;
    private final File file;
    private final long fileSize;
    private final DownloaderPane downloaderPane;
    private final ProgressBar progressBar;
    private long nowDownloadedSize = 0L;

    public Downloader(Frame frame, JSONFile jsonFile, File savePath) {

        this.url = jsonFile.getURL();
        this.file = savePath.isDirectory() ? new File(savePath + File.separator + jsonFile.getName()) : savePath;
        this.fileSize = jsonFile.getSize();
        this.downloaderPane = new DownloaderPane();
        this.progressBar = downloaderPane.getProgressBar();

        frame.setPane(downloaderPane);
    }

    public void downloadFile() throws IOException {

        System.out.println("Download from " + url + " to " + file);

        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {

            InputStream inputStream = new BufferedInputStream(new URL(url.replaceAll(" ", "%20")).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bufferSize;

            while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fileOutputStream.write(buffer, 0, bufferSize);
                nowDownloadedSize += bufferSize;
                progressBar.setValue((int) (nowDownloadedSize * 100 / fileSize));
            }

            inputStream.close();
            fileOutputStream.close();

        } else
            MessageUtil.printError("Ошибка загрузки файлов", "Неудалось создать дирректорию для файла:\n" + file.toString(), true);

        progressBar.setVisible(false);
    }

    public File getFile() {
        return file;
    }

    public DownloaderPane getDownloaderPane() {
        return downloaderPane;
    }

}
