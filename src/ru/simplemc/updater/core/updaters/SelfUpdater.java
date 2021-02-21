package ru.simplemc.updater.core.updaters;

import ru.simplemc.updater.core.Downloader;
import ru.simplemc.updater.data.json.JSONFile;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.util.CryptUtil;
import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.PathUtil;
import ru.simplemc.updater.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelfUpdater {

    private final JSONFile updateInfo;
    private final Frame frame;
    private final File file;

    public SelfUpdater(Frame frame, JSONFile updateInfo) {
        this.updateInfo = updateInfo;
        this.frame = frame;
        this.file = PathUtil.getUpdaterApplicationPath();
    }

    public void checkForUpdate() {

        if (!CryptUtil.compareFileHash(file, updateInfo.getMd5Hash()) && PathUtil.isExecutableFile(file)) {
            try {

                Downloader downloader = new Downloader(frame, updateInfo, file);
                downloader.getDownloaderPane().setStatusAndDescription("Обновление", "Скачивание новой версии программы...");
                downloader.downloadFile();
                downloader.getDownloaderPane().setStatusAndDescription("Обновление готово", "Перезапуск программы...");

                SystemUtil.threadSleep(500);
                restartProgram();

            } catch (IOException e) {
                MessageUtil.printException("Неудалось обновить программу!", "Произошла ошибка во время загрузки файла:\n" + file.toString(), e);
                e.printStackTrace();
            }
        }
    }

    private void restartProgram() {

        List<String> params = new ArrayList<>();

        params.add("java");
        params.add("-jar");
        params.add(file.getPath());

        ProcessBuilder builder = new ProcessBuilder(params);
        builder.redirectErrorStream(true);
        builder.directory(PathUtil.getStorageDirectoryPath());

        try {
            builder.start();
        } catch (IOException e) {
            MessageUtil.printException("Ошибка при запуске - " + file.getName() + "!", "Неудалось запусть исполняемый файл:\n" + file.getPath() + "\n\nПопробуйте запустить его снова.\n", e);
        }

        SystemUtil.halt();
    }
}
