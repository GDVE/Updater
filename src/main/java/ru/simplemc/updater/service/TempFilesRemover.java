package ru.simplemc.updater.service;

import ru.simplemc.updater.utils.FilesUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.nio.file.Paths;

public class TempFilesRemover {

    public static void removeTempFiles() {
        FilesUtils.deleteFilesRecursive(Paths.get(ProgramUtils.getStoragePath() + "/runtime/jre1.8.0_144/"));
    }

}
