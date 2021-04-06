package ru.simplemc.updater.downloader.file;

import lombok.Getter;
import ru.simplemc.updater.thread.data.FileInfo;
import ru.simplemc.updater.utils.CryptUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloaderFile {

    @Getter
    private final Path path;
    @Getter
    private final DownloaderFileType type;
    @Getter
    private final String url;
    @Getter
    private final long size;
    @Getter
    private final String md5;

    public DownloaderFile(FileInfo fileInfo) {
        System.out.println(fileInfo.getUrl());
        this.type = fileInfo.getUrl().contains("/runtime/") ? DownloaderFileType.RUNTIME : fileInfo.getUrl().contains("SimpleMinecraft.") ? DownloaderFileType.UPDATER : DownloaderFileType.LAUNCHER;
        this.path = this.type.equals(DownloaderFileType.UPDATER) ? ProgramUtils.getProgramPath() : Paths.get(ProgramUtils.getStoragePath() + fileInfo.getPath().replace("/program/", "/"));
        this.url = fileInfo.getUrl();
        this.size = fileInfo.getSize();
        this.md5 = fileInfo.getMd5();
    }

    /**
     * @return возвращает True если необходимо перекачать файл
     */
    public boolean isInvalid() {
        return !CryptUtils.md5(path).equals(md5);
    }

    /**
     * Подготавливает файл перед его загрузкой
     *
     * @throws IOException в случае отсутсвия доступа или по инным причинам
     */
    public void prepareBeforeDownload() throws IOException {

        Path parentDir = this.path.getParent();

        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
    }
}
