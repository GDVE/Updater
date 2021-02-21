package ru.simplemc.updater.core.updaters;

import ru.simplemc.updater.core.Downloader;
import ru.simplemc.updater.core.LauncherExecutor;
import ru.simplemc.updater.data.json.JSONFile;
import ru.simplemc.updater.ui.Frame;
import ru.simplemc.updater.util.CryptUtil;
import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LauncherUpdater {

    private final JSONFile jsonFile;
    private final Frame frame;
    private final File file;

    private LauncherExecutor launcherExecutor;

    public LauncherUpdater(Frame frame, JSONFile jsonFile) {
        this.jsonFile = jsonFile;
        this.frame = frame;
        this.file = new File(PathUtil.getStorageDirectoryPath(), "Launcher.jar");
    }

    public void checkForUpdate(RuntimeUpdater runtimeUpdater) {
        if (!CryptUtil.compareFileHash(file, jsonFile.getMd5Hash())) {

            Downloader downloader = new Downloader(frame, jsonFile, file.getParentFile());

            try {
                downloader.getDownloaderPane().setStatusAndDescription("Обновление", "Скачивание новой версии лаунчера...");
                downloader.downloadFile();
            } catch (IOException e) {
                MessageUtil.printException("Неудалось обновить лаунчер!", "Произошла ошибка во время загрузки файла:\n" + file.toString(), e);
                e.printStackTrace();
            }
        }

        launcherExecutor = new LauncherExecutor(frame, file, runtimeUpdater);
    }

    public void execute(){
        launcherExecutor.execute();
    }
}
