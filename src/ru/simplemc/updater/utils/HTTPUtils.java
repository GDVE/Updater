package ru.simplemc.updater.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtils {

    /**
     * @param hostname - имя хоста на который отправляем запрос
     * @param path     - путь до исполняемого файла на хосте
     * @param params   - JSON объект с данными для отправки на хост
     * @return - возвращает JSONObject в случае успеха
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    public static JSONObject get(String hostname, String path, JSONObject params) throws Exception {

        HttpURLConnection connection = openConnection(hostname, path);
        connection.disconnect();

        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(params.toJSONString());
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

        try {
            return (JSONObject) new JSONParser().parse(stringBuffer.toString());
        } catch (ParseException e) {
            e.setUnexpectedObject(stringBuffer.toString());
            throw e;
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
        connection.setRequestProperty("User-Agent", "SimpleMinecraft.Ru Updater");
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;
    }

}
