package ru.simplemc.updater.downloader.file;

import org.json.simple.JSONObject;
import ru.simplemc.updater.Settings;
import ru.simplemc.updater.utils.CryptUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloaderFile {

    private final Path path;
    private final String url;
    private final long size;
    private final String md5;

    public DownloaderFile(JSONObject fileInfoJSON) {
        this.path = fileInfoJSON.get("path").equals("%updater_path%") ? ProgramUtils.getProgramPath() : Paths.get(ProgramUtils.getStoragePath() + "/" + fileInfoJSON.get("path"));
        this.url = Settings.HTTP_ADDRESS + String.valueOf(fileInfoJSON.get("url")).replaceAll(" ", "%20");
        this.size = Long.parseLong(String.valueOf(fileInfoJSON.get("size")));
        this.md5 = String.valueOf(fileInfoJSON.get("md5"));
    }

    public Path getPath() {
        return path;
    }

    public String getUrl() {

        if (url.endsWith(".jar") || url.endsWith(".exe"))
            return url + "?" + System.currentTimeMillis();

        return url;
    }

    public long getSize() {
        return size;
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
