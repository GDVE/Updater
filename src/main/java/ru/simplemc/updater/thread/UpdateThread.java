package ru.simplemc.updater.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.simplemc.updater.Settings;
import ru.simplemc.updater.downloader.Downloader;
import ru.simplemc.updater.downloader.file.DownloaderFileType;
import ru.simplemc.updater.downloader.file.DownloaderRuntimeArchiveFile;
import ru.simplemc.updater.executor.LauncherExecutor;
import ru.simplemc.updater.executor.UpdaterExecutor;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.thread.data.UpdaterRequest;
import ru.simplemc.updater.thread.data.UpdaterResponse;
import ru.simplemc.updater.utils.HTTPUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateThread extends Thread {

    private final Frame frame;
    private final UpdaterExecutor updaterExecutor;

    public UpdateThread(Frame frame) {
        this.setName("Updater Thread");
        this.frame = frame;
        this.updaterExecutor = UpdaterExecutor.init(frame);
    }

    @Override
    public void run() {

        UpdaterResponse response = getUpdaterServerResponse();

        if (response == null) {
            MessageUtils.printErrorWithShutdown("Ошибка при обработке данных от сервера", "Неудалось обработать ни один параметр...");
            return;
        }

        AtomicReference<Path> runtimeExecutableFilePath = new AtomicReference<>();
        AtomicReference<Path> launcherExecutableFilePath = new AtomicReference<>();

        response.getDownloaderFiles().forEach(downloaderFile -> {
            if (downloaderFile.isInvalid()) {

                if (downloaderFile.getType().equals(DownloaderFileType.UPDATER) && ProgramUtils.isDebugMode()) {
                    return;
                }

                if (downloaderFile.getType().equals(DownloaderFileType.UPDATER) && OSUtils.isMacOS()) {
                    MessageUtils.printSuccess("Вышло обновление программы!", "Для вас доступна новая версия программы! Скачайте ее с нашего сайта.\nПосле нажатия кнопки ОК откроется страница загрузки программы.");
                    OSUtils.openLinkInSystemBrowser("https://simpleminecraft.ru/download.html");
                    ProgramUtils.haltProgram();
                    return;
                }

                try {
                    new Downloader(this.frame, downloaderFile).process();
                } catch (Exception e) {
                    MessageUtils.printFullStackTraceWithExit("Не удалось загрузить файл " + downloaderFile.getPath().getFileName().toString(), e);
                }

                if (downloaderFile.getType().equals(DownloaderFileType.UPDATER)) {
                    this.updaterExecutor.repaintFrame();
                    try {
                        this.updaterExecutor.execute();
                    } catch (IOException e) {
                        MessageUtils.printFullStackTraceWithExit("Не удалось перезапустить программу!", e);
                    }
                }
            }

            if (downloaderFile.getType().equals(DownloaderFileType.RUNTIME)) {
                runtimeExecutableFilePath.set(((DownloaderRuntimeArchiveFile) downloaderFile).getRuntimeExecutableFile());
            }

            if (downloaderFile.getType().equals(DownloaderFileType.LAUNCHER)) {
                launcherExecutableFilePath.set(downloaderFile.getPath());
            }
        });

        LauncherExecutor launcherExecutor = new LauncherExecutor(frame, runtimeExecutableFilePath.get().toString(), launcherExecutableFilePath.get().toString());
        try {
            launcherExecutor.execute();
        } catch (IOException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось запустить лаунчер!", e);
        }
    }

    private UpdaterResponse getUpdaterServerResponse() {

        UpdaterRequest updaterRequest = new UpdaterRequest();
        updaterRequest.setSystemId(OSUtils.getSystemIdWithArch());
        updaterRequest.setApplicationFormat(ProgramUtils.getExecutableFileExtension());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(HTTPUtils.post(Settings.API_DOMAIN, "/launcher/updater/check", updaterRequest), UpdaterResponse.class);
        } catch (Exception e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось получить ответ от сервера!", e);
            return null;
        }
    }
}
