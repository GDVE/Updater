package ru.simplemc.updater.service.downloader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FilesMapping extends HashMap<String, String> {

    @Getter
    private final Path scanPath;
    @Getter
    private final Path mappingFilePath;

    public FilesMapping(Path scanPath, String name) {
        this.scanPath = scanPath;
        this.mappingFilePath = Paths.get(scanPath + "/" + name + ".json");
        try {
            this.readFromDisk();
        } catch (IOException ignored) {
        }
    }

    private String getFileKey(Path filePath) {
        return filePath.toString().replace(scanPath.toString(), "");
    }

    public void put(Path path, String md5) {
        this.put(getFileKey(path), md5);
    }

    public void put(Path path, long size) {
        this.put(getFileKey(path), String.valueOf(size));
    }

    public boolean findInvalidOrDeletedFiles() throws IOException {
        for (String key : this.keySet()) {
            if (key.equals("archiveHash")) {
                continue;
            }
            Path path = Paths.get(scanPath + "/" + key);
            if (!Files.exists(path) || !String.valueOf(Files.size(path)).equals(get(key))) {
                return true;
            }
        }

        return false;
    }

    public boolean isInvalid(DownloaderFile file) {
        if (!Files.exists(file.getPath())) {
            return true;
        }

        String mappingMd5 = get(getFileKey(file.getPath()));
        return mappingMd5 == null || !mappingMd5.equals(file.getMd5());
    }

    public boolean isInvalid(Path path) {

        if (mappingFilePath.equals(path)) {
            return false;
        }

        if (!Files.exists(path)) {
            return true;
        }

        String identifier = get(getFileKey(path));
        if (identifier == null) {
            return true;
        }

        try {
            return !identifier.equals(String.valueOf(Files.size(path)));
        } catch (IOException e) {
            return true;
        }
    }

    public void scanAndWriteToDisk() throws IOException {

        Stream<Path> stream = Files.find(scanPath, Integer.MAX_VALUE,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile());

        stream.forEach(path -> {
            try {
                this.put(path, Files.size(path));
            } catch (IOException e) {
                Updater.getLogger().error("Failed to scan file " + path + ":", e);
                MessageUtils.printErrorWithShutdown("Произошла ошибка",
                        "Не удалось сохранить информацию о файле:\n" + path);
            }
        });

        this.writeToDisk();
    }

    public void readFromDisk() throws IOException {

        if (!Files.exists(mappingFilePath)) {
            return;
        }

        this.clear();
        this.putAll(new ObjectMapper().readValue(mappingFilePath.toFile(), new TypeReference<Map<String, String>>() {
        }));
    }

    public void writeToDisk() throws IOException {
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(mappingFilePath.toFile(), this);
    }

    public void removeFromDisk() throws IOException {
        Files.deleteIfExists(mappingFilePath);
    }
}
