package ru.simplemc.updater.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplemc.updater.Settings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtils {

    /**
     * @param path   - путь до исполняемого файла на хосте
     * @param params - JSON объект с данными для отправки на хост
     * @return - возвращает JSONObject в случае успеха
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    public static JSONObject post(String path, JSONObject params) throws Exception {

        String response = post(path, params.toJSONString());

        try {
            return (JSONObject) new JSONParser().parse(response);
        } catch (ParseException e) {
            e.setUnexpectedObject(response);
            throw e;
        }
    }

    /**
     * Отправлет пост запрос на сервер
     *
     * @param path   - путь до исполняемого файла на хосте
     * @param params - JSON строка с данными для отправки на сервер
     * @return - возвращает JSONObject в случае успеха
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    private static String post(String path, String params) throws Exception {
        try {

            HttpURLConnection connection = openConnection(Settings.HTTP_ADDRESS, path);
            connection.disconnect();

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(params);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream connectionInputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuffer = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null)
                stringBuffer.append(line);

            connectionInputStream.close();
            bufferedReader.close();

            return stringBuffer.toString();

        } catch (Exception e) {
            if (switchToHTTPS()) return post(path, params);
            else throw e;
        }
    }

    /**
     * @param hostname - имя хоста на который отправляем запрос
     * @param path     - путь до исполняемого файла на хосте
     * @return - возвращает HttpURLConnection для дальнейшей обработки
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    private static HttpURLConnection openConnection(String hostname, String path) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(hostname + path).openConnection();
        connection.setRequestProperty("User-Agent", Settings.HTTP_USER_AGENT);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;
    }

    /**
     * @return Возвращает текущий HTTP протокол с которым работает лаунчер на данный момент
     */
    public static String getProtocol() {
        return Settings.HTTP_ADDRESS.startsWith("https://") ? "https" : "http";
    }

    /**
     * @return возвращает TRUE при успешном переключении на протокол HTTPS
     */
    private static boolean switchToHTTPS() {

        if (getProtocol().equals("http")) {
            Settings.HTTP_ADDRESS = Settings.HTTP_ADDRESS.replace("http://", "https://");
            return true;
        }

        return false;
    }

}
