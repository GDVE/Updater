package ru.simplemc.updater.downloader.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.thread.data.FileInfo;
import ru.simplemc.updater.utils.CompressedUtils;
import ru.simplemc.updater.utils.FileUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DownloaderRuntimeArchiveFile extends DownloaderFile {

    private final Path runtimeDirectory;
    private final Path runtimeExecutableFile;
    private final Path runtimeFilesScheme;

    public DownloaderRuntimeArchiveFile(FileInfo fileInfo) throws ClassCastException {
        super(fileInfo);
        Path runtimesStorage = Paths.get(ProgramUtils.getStoragePath() + "/runtime");
        this.runtimeDirectory = Paths.get(runtimesStorage + "/" + (OSUtils.isMacOS() ? "jre1.8.0_51.jre" : "jre1.8.0_51"));
        this.runtimeExecutableFile = Paths.get(runtimeDirectory + (OSUtils.isMacOS() ? "/Contents/Home/bin/" : "/bin/") + (OSUtils.isWindows() ? "java.exe" : "java"));
        this.runtimeFilesScheme = Paths.get(runtimesStorage + "/" + runtimeDirectory.getFileName().toString() + ".json");
    }

    /**
     * Распаковывает архив с файлами
     */
    public void unpack() throws IOException {

        if (getPath().getFileName().toString().endsWith(".zip")) {
            CompressedUtils.unZipArchive(getPath().toFile());
        } else {
            File archiveFile = this.getPath().toFile();
            Archiver archiver = ArchiverFactory.createArchiver(archiveFile);
            archiver.extract(archiveFile, archiveFile.getParentFile());
        }

        Files.deleteIfExists(this.getPath());
        this.createFilesScheme();

        if (OSUtils.isMacOS()) {
            this.resolveFilesPermissions();
        }
    }

    /**
     * Создает файл-схему из файлов JRE (необходимо для более рациональной и быстрой проверки)
     *
     * @throws IOException - возвращает при невозможности записи файла-схемы
     */
    private void createFilesScheme() throws IOException {
        Map<String, Long> filesScheme = new HashMap<>();
        Files.walk(runtimeDirectory).filter(Files::isRegularFile).forEach(path -> {
            try {
                filesScheme.put(path.toString().replace(runtimeDirectory.toString(), ""), Files.size(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.write(runtimeFilesScheme, new ObjectMapper().writeValueAsBytes(filesScheme));
    }

    @Override
    public void prepareBeforeDownload() throws IOException {

        super.prepareBeforeDownload();

        if (Files.exists(runtimeDirectory)) {
            FileUtils.deleteFilesRecursive(runtimeDirectory);
        }
    }

    @Override
    public boolean isInvalid() {

        if (!Files.exists(this.runtimeDirectory) || !Files.exists(this.runtimeExecutableFile) || !Files.exists(this.runtimeFilesScheme)) {
            return true;
        }

        try {
            Map<String, Long> jsonObject = new ObjectMapper().readValue(new String(Files.readAllBytes(this.runtimeFilesScheme)), new TypeReference<Map<String, Long>>() {
            });
            if (jsonObject.size() < 10) {
                MessageUtils.printWarning("Обнаружена проблема", "Недостаточно исполняемых файлов Java!");
                return true;
            }

            for (String filePath : jsonObject.keySet()) {
                Path runtimeFilePath = Paths.get(runtimeDirectory + "/" + filePath);
                if (!Files.exists(runtimeFilePath) || Files.size(runtimeFilePath) != jsonObject.get(filePath)) {
                    MessageUtils.printWarning("Обнаружена проблема", "Найден невалидный файл Java:\n" + runtimeFilePath);
                    return true;
                }
            }
        } catch (IOException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось проверить исполняемые файлы Java!", e);
            return true;
        }

        return false;
    }

    /**
     * @return Возвращает путь до исполняемого файла JRE
     */
    public Path getRuntimeExecutableFile() {
        return runtimeExecutableFile;
    }

    /**
     * Необходимо для запуска лаунчера на MacOS при использовании свободных сборок JRE (azul, например)
     */
    public void resolveFilesPermissions() {
        try {
            Files.walk(this.runtimeDirectory).filter(Files::isRegularFile).forEach(path -> {
                try {
                    Set<PosixFilePermission> perms = Files.readAttributes(path, PosixFileAttributes.class).permissions();
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    perms.add(PosixFilePermission.OWNER_READ);
                    perms.add(PosixFilePermission.OWNER_EXECUTE);
                    Files.setPosixFilePermissions(path, perms);
                } catch (IOException e) {
                    MessageUtils.printFullStackTraceWithExit("Не удалось установить права на исполняемые файлы", e, false);
                }
            });
        } catch (IOException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось запустить установку прав на исполняемые файлы", e, false);
        }
    }
}
