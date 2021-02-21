package ru.simplemc.updater.core.updaters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplemc.updater.core.Downloader;
import ru.simplemc.updater.core.RuntimeInstaller;
import ru.simplemc.updater.data.json.JSONFile;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.SystemUtil;

import java.io.*;

public class RuntimeUpdater {

    private final Frame frame;
    private final JSONFile runtimeData;
    private final RuntimeInstaller installer;

    public RuntimeUpdater(Frame frame, JSONFile fileData) {
        this.frame = frame;
        this.runtimeData = fileData;
        this.installer = new RuntimeInstaller();
    }

    public void checkForUpdate() {

        if (isNeedRuntimeUpdate()) {

            installer.uninstall();

            Downloader downloader = new Downloader(frame, runtimeData, installer.getRuntimesStorage());
            downloader.getDownloaderPane().setStatusAndDescription("Обновление", "Скачивание Java Runtime Environment...");

            try {

                downloader.downloadFile();
                downloader.getDownloaderPane().setStatusAndDescription("Обновление готово", "Установка Java Runtime Environment...");

            } catch (IOException e) {
                MessageUtil.printException("Неудалось обновить программу!", "Произошла ошибка во время загрузки файла:\n" + downloader.getFile(), e);
                e.printStackTrace();
                return;
            }

            installer.install(downloader.getFile());
        }
    }

    private boolean isNeedRuntimeUpdate() {

        File oldRuntimeDirectory = new File(installer.getRuntimesStorage(), SystemUtil.isWindows() ? "jre-8-51-x" + SystemUtil.getSystemArch() : "jre1.8.0_191");

        if (oldRuntimeDirectory.exists() && oldRuntimeDirectory.renameTo(installer.getRuntimeDirectory())) {
            installer.createJSONScheme();
        }

        if (!installer.getRuntimeSchemeJSON().exists()) {
            System.out.println("Runtime scheme json file not found!");
            return true;
        }

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(installer.getRuntimeSchemeJSON()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(stringBuilder.toString());

            if (jsonObject != null) {

                if (jsonObject.size() < 10) {
                    System.out.println("Invalid count of runtime files!");
                    return true;
                }

                for (Object filePath : jsonObject.keySet()) {

                    File runtimeFile = new File(installer.getRuntimeDirectory() + filePath.toString());

                    if (!runtimeFile.exists() || runtimeFile.length() != Long.parseLong(jsonObject.get(filePath).toString())) {
                        System.out.println("Found a invalid file of runtime: " + runtimeFile);
                        return true;
                    }
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public RuntimeInstaller getInstaller() {
        return installer;
    }
}
