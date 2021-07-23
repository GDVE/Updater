package ru.simplemc.updater.service.downloader.beans;

import lombok.Getter;
import ru.simplemc.updater.utils.CryptUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class DownloaderFile {

    @Getter
    private final Path path;
    @Getter
    private final String name;
    @Getter
    private final String url;
    @Getter
    private final String md5;
    @Getter
    private final Long size;

    public DownloaderFile(FileInfo fileInfo) {
        path = Paths.get(ProgramUtils.getStoragePath() + "/" + fileInfo.getPath()
                .replace("/program/", ""));
        name = fileInfo.getName();
        url = fileInfo.getUrl();
        md5 = fileInfo.getMd5();
        size = fileInfo.getSize();
    }

    public boolean isInvalid() {
        return !md5.equals(CryptUtils.md5(getPath()));
    }

    public void prepareBeforeDownload() throws IOException {
        Path parent = getPath().getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    public void prepareAfterDownload() throws IOException {
    }
}
