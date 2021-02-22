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

        String response;

        try {
            response = post(path, params.toJSONString());
        } catch (Exception e) {
            if (Settings.HTTP_ADDRESS.contains(".ru")) {
                Settings.HTTP_ADDRESS = Settings.HTTP_ADDRESS.replace(".ru", ".net");
                response = post(path, params.toJSONString());
            } else
                throw e;
        }

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
     * @param path       - путь до исполняемого файла на хосте
     * @param jsonParams - JSON объект с данными для отправки на хост
     * @return - возвращает JSONObject в случае успеха
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    private static String post(String path, String jsonParams) throws Exception {

        HttpURLConnection connection = openConnection(Settings.HTTP_ADDRESS, path);
        connection.disconnect();

        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(jsonParams);
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
    }

    /**
     * @param hostname - имя хоста на который отправляем запрос
     * @param path     - путь до исполняемого файла на хосте
     * @return - возвращает HttpURLConnection для дальнейшей обработки
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    private static HttpURLConnection openConnection(String hostname, String path) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(hostname + path).openConnection();
        connection.setRequestProperty("User-Agent", "SimpleMinecraft.Ru Updater");
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;
    }

}
