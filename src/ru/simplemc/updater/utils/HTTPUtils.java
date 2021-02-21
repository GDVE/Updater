package ru.simplemc.updater.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtils {

    public static JSONObject get(String method, String hostname, String path, JSONObject params) throws Exception {

        HttpURLConnection connection = openConnection(method, hostname, path);
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

        return (JSONObject) new JSONParser().parse(stringBuffer.toString());
    }

    private static HttpURLConnection openConnection(String method, String hostName, String path) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(hostName + path).openConnection();
        connection.setRequestProperty("User-Agent", "SimpleMinecraft.Ru Updater");
        connection.setRequestMethod(method);
        connection.setUseCaches(false);

        if (method.equals("POST")) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
        }

        return connection;
    }

}
