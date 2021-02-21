package ru.simplemc.updater.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.simplemc.updater.core.updaters.RuntimeUpdater;
import ru.simplemc.updater.core.updaters.LauncherUpdater;
import ru.simplemc.updater.core.updaters.SelfUpdater;
import ru.simplemc.updater.data.json.JSONFile;
import ru.simplemc.updater.ui.Frame;
import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.PathUtil;
import ru.simplemc.updater.util.SystemUtil;
import ru.simplemc.updater.util.WebUtils;

import java.util.HashMap;
import java.util.Map;

public class MainThread implements Runnable {

    private final Frame frame;

    public MainThread(Frame frame) {
        this.frame = frame;
    }

    @Override
    public void run() {

        String rawJsonResponse = WebUtils.getPostResponse("/launcher/updater.php", "&system_id=" + SystemUtil.getSystemIdWithArch() + "&updater_format=" + PathUtil.getExecutableFileExtension(PathUtil.getUpdaterApplicationPath()));
        Map<String, JSONFile> updaterData = parseUpdaterData(rawJsonResponse);

        if (updaterData.isEmpty())
            MessageUtil.printError("Ошибка при обработке данных от сервера", "Неудалось обработать ни один параметр...");

        SelfUpdater selfUpdater = new SelfUpdater(frame, updaterData.get("updater"));
        selfUpdater.checkForUpdate();

        RuntimeUpdater runtimeUpdater = new RuntimeUpdater(frame, updaterData.get("runtime"));
        runtimeUpdater.checkForUpdate();

        LauncherUpdater launcherUpdater = new LauncherUpdater(frame, updaterData.get("launcher"));
        launcherUpdater.checkForUpdate(runtimeUpdater);
        launcherUpdater.execute();
    }

    private Map<String, JSONFile> parseUpdaterData(String rawJsonResponse) {

        JSONParser jsonParser = new JSONParser();
        Map<String, JSONFile> updateData = new HashMap<>();

        try {

            JSONArray updaterDataJSON = (JSONArray) jsonParser.parse(rawJsonResponse);

            for (Object object : updaterDataJSON) {

                JSONObject jsonObject = (JSONObject) object;
                String selector = jsonObject.get("filename").toString().split("\\.")[0].toLowerCase();

                if (selector.startsWith("jre-8u"))
                    selector = "runtime";

                updateData.put(selector, new JSONFile(jsonObject));
            }

        } catch (Exception e) {
            MessageUtil.printException("Ошибка обработки данных с сервера!", "При обработке ответа от сервера возникла ошибка.\nПроверьте соединение с интернетом!\n\nОтвет от сервера:\n" + rawJsonResponse, e);
        }

        return updateData;
    }
}
