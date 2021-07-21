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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LauncherRuntime extends DownloaderFile {

    @Getter
    private final Path directory;
    @Getter
    private final String version;
    @Getter
    private final FilesMapping mapping;

    public LauncherRuntime(FileInfo fileInfo) {
        super(fileInfo);
        this.version = "1.8.0_144";
        this.directory = Paths.get(ProgramUtils.getStoragePath() + "/runtime/jre"
                + (OSUtils.isMacOS() ? version + ".jre" : version));
        this.mapping = new FilesMapping(directory, "runtime_" + version);
    }

    @Override
    public boolean isInvalid() {

        if (!this.getMd5().equals(mapping.get("archiveHash"))) {
            return true;
        }

        Stream<Path> stream;
        try {
            stream = Files.find(directory, Integer.MAX_VALUE, (path, attributes) -> attributes.isRegularFile());
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        for (Path path : stream.collect(Collectors.toList())) {
            if (mapping.isInvalid(path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void prepareBeforeDownload() throws IOException {
        mapping.removeFromDisk();
        Files.createDirectories(directory.getParent());
    }

    @Override
    public void prepareAfterDownload() throws IOException {

        File archiveFile = getPath().toFile();
        File extractionDirectory = directory.getParent().toFile();

        if (this.getName().endsWith(".zip")) {
            CompressedUtils.unZipArchive(archiveFile, extractionDirectory);
        } else {
            Archiver archiver = ArchiverFactory.createArchiver(archiveFile);
            archiver.extract(archiveFile, extractionDirectory);
        }

        mapping.put("archiveHash", this.getMd5());
        mapping.scanAndWriteToDisk();
        FilesUtils.deleteFile(getPath());

        if (OSUtils.isMacOS()) this.resolveFilesPermissions();
    }

    public String getExecutablePath() throws IOException {
        return Files.find(directory,
                Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile()
                        && path.getFileName().toString().endsWith("java")
                        || path.getFileName().toString().endsWith("java.exe"))
                .collect(Collectors.toList()).stream().findFirst()
                .map(Path::toString).orElse(null);
    }

    private void resolveFilesPermissions() {

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
