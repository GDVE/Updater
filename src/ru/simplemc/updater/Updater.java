package ru.simplemc.updater;

import org.json.simple.JSONObject;
import ru.simplemc.updater.utils.HTTPUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Updater {

    public static void main(String... args) {

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("http.agent", Settings.HTTP_USER_AGENT);

        try {

            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);

            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = defaultToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(defaultToolkit, Settings.FRAME_TITLE);

        } catch (Throwable ignored) {
        }

        /*
        Config launcher_config = new Config("launcher", false);

        if (launcher_config.hasValue("launcherSelectedTheme")) {

            String themeName = launcher_config.getString("launcherSelectedTheme").replace("Тема оформления: ", "");

            switch (themeName) {
                case "Осень":
                    Settings.BACKGROUND_IMAGE = "fall";
                    break;
                case "Каньон":
                    Settings.BACKGROUND_IMAGE = "canyon";
                    break;
                case "Ночное небо":
                    Settings.BACKGROUND_IMAGE = "night";
                    break;
                case "Рассвет":
                    Settings.BACKGROUND_IMAGE = "dawn";
                    break;
                case "Озеро":
                    Settings.BACKGROUND_IMAGE = "lake";
                    break;
                case "Крепость":
                    Settings.BACKGROUND_IMAGE = "fortress";
                    break;
                case "Закат":
                    Settings.BACKGROUND_IMAGE = "sunset";
                    break;
                case "Лес":
                    Settings.BACKGROUND_IMAGE = "forest";
                    break;
                default:
                    Settings.BACKGROUND_IMAGE = "winter";
                    break;
            }
        }

        if (Settings.BACKGROUND_IMAGE.equals("winter")) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
            String currentHour = simpleDateFormat.format(System.currentTimeMillis());
            String[] nightHours = {"17", "18", "19", "20", "21", "22", "23", "00", "01", "02", "03", "04", "05", "06"};

            for (String nightHour : nightHours) {
                if (currentHour.equals(nightHour)) {
                    Settings.BACKGROUND_IMAGE = Settings.BACKGROUND_IMAGE.replace("winter", "winter_night");
                }
            }
        }

        Frame frame = new Frame(Settings.FRAME_TITLE);
        frame.setPane(new WelcomePane());

        Thread updaterThread = new Thread(new MainThread(frame));
        updaterThread.start();
         */

        try {
            JSONObject jsonObject = HTTPUtils.get("POST", Settings.HTTP_ADDRESS, "/launcher/updater.php", new JSONObject());
            System.out.println(jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
