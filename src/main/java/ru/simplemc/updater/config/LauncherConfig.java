package ru.simplemc.updater.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Короткая версия конфигурации лаунчера
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LauncherConfig implements JsonConfig<LauncherConfig> {

    @JsonIgnore
    private final Path path = Paths.get(ProgramUtils.getStoragePath() + "/launcher.json");

    @JsonProperty
    private String selectedTheme = null;

    public void readFromDisk() throws IOException, NoSuchFieldException, IllegalAccessException {
        this.readFromDisk(path);
    }

    public void writeToDisk() throws IOException {
        this.writeToDisk(this, path);
    }
}
