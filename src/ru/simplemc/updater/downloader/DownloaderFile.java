package ru.simplemc.updater.downloader;

import org.json.simple.JSONObject;
import ru.simplemc.updater.utils.ProgramUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloaderFile {

    private final Path path;
    private final String url;
    private final long size;

    public DownloaderFile(JSONObject fileInfoJSON) throws ClassCastException {
        this.path = Paths.get(ProgramUtils.getStoragePath() + "/" + fileInfoJSON.get("path"));
        this.url = String.valueOf(fileInfoJSON.get("url")).replaceAll(" ", "%20");
        this.size = (long) fileInfoJSON.get("size");
    }

    public Path getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public long getSize() {
        return size;
    }
}
