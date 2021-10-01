package ru.simplemc.updater.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.simplemc.updater.Environment;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.executor.LauncherExecutor;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.service.downloader.DownloaderService;
import ru.simplemc.updater.service.downloader.beans.DownloaderFile;
import ru.simplemc.updater.service.downloader.files.LauncherFile;
import ru.simplemc.updater.service.downloader.files.LauncherRuntime;
import ru.simplemc.updater.service.downloader.files.UpdaterFile;
import ru.simplemc.updater.service.http.HttpServiceManager;
import ru.simplemc.updater.service.http.beans.CheckUpdatesRequest;
import ru.simplemc.updater.service.http.beans.CheckUpdatesResponse;
import ru.simplemc.updater.service.http.beans.TypedResponse;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateThread extends Thread {

    public UpdateThread() {
        this.setName("Updater Thread");
    }

    @Override
    public void run() {

        Optional<CheckUpdatesResponse> optionalResponse = getCheckUpdatesResponse();

        if (optionalResponse.isPresent()) {

            Updater.getFrame().setStatus("Поиск обновлений", "Проверка файлов...");
            CheckUpdatesResponse response = optionalResponse.get();

            List<DownloaderFile> downloaderFiles = new ArrayList<>();
            downloaderFiles.add(new UpdaterFile(response.getUpdaterFileInfo()));
            downloaderFiles.add(new LauncherFile(response.getLauncherFileInfo()));
            downloaderFiles.add(new LauncherRuntime(response.getRuntimesFileInfos()));
            downloaderFiles.stream()
                    .filter(DownloaderFile::isInvalid)
                    .forEach(downloaderFile -> {
                        DownloaderService downloaderService = new DownloaderService(downloaderFile);
                        try {
                            downloaderService.process();
                        } catch (IOException e) {
                            e.printStackTrace();
                            MessageUtils.printFullStackTraceWithExit("Не удалось загрузить файл: "
                                    + downloaderFile.getName(), e);
                        }
                    });

            LauncherFile launcherFile = (LauncherFile) downloaderFiles.get(1);
            LauncherRuntime launcherRuntime = (LauncherRuntime) downloaderFiles.get(2);

            try {
                LauncherExecutor executor = new LauncherExecutor(
                        Updater.getFrame(),
                        launcherRuntime.getExecutablePath(), launcherFile.getPath().toString()
                );
                executor.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Updater.getFrame().setStatus("Произошла ошибка", "Не подключится к серверу!");
        }

        ProgramUtils.haltProgram();
    }

    private Optional<CheckUpdatesResponse> getCheckUpdatesResponse() {

        CheckUpdatesRequest request = new CheckUpdatesRequest();
        request.setSystemId(OSUtils.getSystemIdWithArch());
        request.setApplicationFormat(ProgramUtils.getExecutableFileExtension());

        String result;
        try {
            result = HttpServiceManager.performPostRequest(Environment.API_DOMAIN + "/launcher/checkUpdates", request);
        } catch (IOException e) {
            MessageUtils.printFullStackTraceWithExit("Произошла ошибка при обработки данных!", e);
            return Optional.empty();
        }

        try {
            return Optional.of(HttpServiceManager.getMapper().readValue(result, CheckUpdatesResponse.class));
        } catch (JsonProcessingException e) {
            try {
                TypedResponse response = HttpServiceManager.getMapper().readValue(result, TypedResponse.class);
                MessageUtils.printErrorWithShutdown(response.getTitle(), response.getMessage());
            } catch (JsonProcessingException e1) {
                MessageUtils.printFullStackTraceWithExit("Произошла ошибка при обработки данных!", e1);
            }
        }

        return Optional.empty();
    }
}
