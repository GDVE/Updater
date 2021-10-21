package ru.simplemc.updater.service.downloader.files;

import lombok.Getter;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import ru.simplemc.updater.service.downloader.FilesMapping;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;
import ru.simplemc.updater.service.downloader.beans.FileInfo;
import ru.simplemc.updater.utils.CompressedUtils;
import ru.simplemc.updater.utils.FilesUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LauncherRuntime extends DownloaderFile {

    @Getter
    private final Path directory;
    @Getter
    private final String version;
    @Getter
    private final FilesMapping mapping;

    public LauncherRuntime(List<FileInfo> runtimesFileInfos) {
        super(findBySystem(runtimesFileInfos));
        this.version = "1.8.0_51";
        this.directory = Paths.get(ProgramUtils.getStoragePath() + "/runtime/jre"
                + (OSUtils.isMacOS() ? version + ".jre" : version));
        this.mapping = new FilesMapping(directory, "runtime_" + version);
    }

    private static FileInfo findBySystem(List<FileInfo> fileInfos) {
        return fileInfos.stream().filter(fileInfo -> {
            if (OSUtils.isWindows()) return fileInfo.getName().endsWith(".zip");
            else return true;
        }).findAny().orElse(fileInfos.get(fileInfos.size() - 1));
    }

    @Override
    public Path getPath() {
        return Paths.get(directory.getParent() + "/" + getName());
    }

    @Override
    public boolean isInvalid() {
        try {
            if (!getExecutablePath().isPresent()) {
                return true;
            }
            if (Files.exists(getPath()) && Files.size(getPath()) != getSize()) {
                return true;
            }
        } catch (IOException e) {
            return true;
        }

        if (!this.getMd5().equals(mapping.get("archiveHash"))) {
            return true;
        }

        Path oldJreMapping = Paths.get(directory + ".json");
        if (Files.exists(oldJreMapping)) {
            FilesUtils.deleteFile(oldJreMapping);
            return true;
        }

        try {
            return mapping.findInvalidOrDeletedFiles();
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public void prepareBeforeDownload() throws IOException {
        mapping.removeFromDisk();
        FilesUtils.deleteFilesRecursive(directory);
        Files.createDirectories(directory.getParent());
    }

    @Override
    public void prepareAfterDownload() throws IOException {

        CompressedUtils.extractArchive(getPath().toFile(), getPath().getParent().toFile());

        mapping.put("archiveHash", this.getMd5());
        mapping.scanAndWriteToDisk();

        resolveFilesPermissions();
        FilesUtils.deleteFile(getPath());
    }

    public Optional<Path> getExecutablePath() throws IOException {
        return Files.find(directory,
                Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile()
                        && path.getFileName().toString().endsWith("java")
                        || path.getFileName().toString().endsWith("java.exe")).findAny();
    }

    private void resolveFilesPermissions() {

        if (!OSUtils.isMacOS()) {
            return;
        }

        Set<PosixFilePermission> permissionSet = new HashSet<>();
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);

        try {
            Files.find(directory, Integer.MAX_VALUE,
                            ((path, attributes) -> attributes.isRegularFile()))
                    .forEach(path -> {
                        try {
                            Files.setPosixFilePermissions(path, permissionSet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
