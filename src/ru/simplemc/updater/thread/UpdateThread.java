package ru.simplemc.updater.thread;

import com.sun.istack.internal.Nullable;
import org.json.simple.JSONObject;
import ru.simplemc.updater.Settings;
import ru.simplemc.updater.gui.Frame;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.utils.HTTPUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

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
        }

        System.out.println(updateData.toJSONString());

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
            MessageUtils.printFullStackTrace("Не удалось получить ответ от сервера!", e, true);
            return null;
        }
    }
}
