package ru.simplemc.updater.thread;

import com.sun.istack.internal.Nullable;
import org.json.simple.JSONObject;
import ru.simplemc.updater.Settings;
import ru.simplemc.updater.downloader.Downloader;
import ru.simplemc.updater.downloader.file.DownloaderFile;
import ru.simplemc.updater.downloader.file.DownloaderRuntimeArchiveFile;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.utils.HTTPUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;

public class UpdateThread extends Thread {

    private final Frame frame;

    public UpdateThread(Frame frame) {
        this.setName("Updater Thread");
        this.frame = frame;
    }

    @Override
    public void run() {

        JSONObject updateData = getUpdateData();

        if (updateData == null) {
            MessageUtils.printErrorWithShutdown("Ошибка при обработке данных от сервера", "Неудалось обработать ни один параметр...");
            return;
        }

        if (updateData.containsKey("title") && updateData.containsKey("message")) {
            MessageUtils.printErrorWithShutdown(String.valueOf(updateData.get("title")), String.valueOf(updateData.get("message")));
            return;
        }

        updateData.forEach((key, value) -> {

            if (!(value instanceof JSONObject)) {
                throw new IllegalArgumentException("Value is not JSONObject, skipping it...");
            }

            DownloaderFile downloaderFile = key.equals("runtime") ? new DownloaderRuntimeArchiveFile((JSONObject) value) : new DownloaderFile((JSONObject) value);

            if (downloaderFile.isInvalid()) {
                try {
                    new Downloader(frame, downloaderFile).process();
                } catch (IOException e) {
                    MessageUtils.printFullStackTraceWithExit("Не удалось загрузить файл " + downloaderFile.getPath().getFileName().toString(), e);
                }
            }

        });

        /*
        SelfUpdater selfUpdater = new SelfUpdater(frame, updaterData.get("updater"));
        selfUpdater.checkForUpdate();

        RuntimeUpdater runtimeUpdater = new RuntimeUpdater(frame, updaterData.get("runtime"));
        runtimeUpdater.checkForUpdate();

        LauncherUpdater launcherUpdater = new LauncherUpdater(frame, updaterData.get("launcher"));
        launcherUpdater.checkForUpdate(runtimeUpdater);
        launcherUpdater.execute();
         */
    }

    @Nullable
    private JSONObject getUpdateData() {

        try {

            JSONObject updaterParams = new JSONObject();
            updaterParams.put("updater_format", ProgramUtils.getExecutableFileExtension());
            updaterParams.put("system_id", OSUtils.getSystemIdWithArch());

            return HTTPUtils.get(Settings.HTTP_ADDRESS, "/launcher/updater.php", updaterParams);

        } catch (Exception e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось получить ответ от сервера!", e);
            return null;
        }
    }
}
