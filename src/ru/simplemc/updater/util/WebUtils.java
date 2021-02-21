package ru.simplemc.updater.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static ru.simplemc.updater.Settings.HTTP_ADDRESS;
import static ru.simplemc.updater.Settings.HTTP_USER_AGENT;

public class WebUtils {

    public static String getPostResponse(String webServerPath, String phpParams) {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(HTTP_ADDRESS + webServerPath).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.disconnect();

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(phpParams);
            dataOutputStream.flush();
            dataOutputStream.close();

            return inputStreamToString(connection.getInputStream());

        } catch (Throwable e) {
            if (isAddressCorrected())
                return getPostResponse(webServerPath, phpParams);
            else
                return null;
        }
    }

    public static String getGetResponse(String webServerPath, String phpParams) {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(HTTP_ADDRESS + webServerPath + "?" + phpParams).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
            connection.setUseCaches(false);
            connection.connect();
            connection.disconnect();

            return inputStreamToString(connection.getInputStream());

        } catch (Throwable e) {
            if (isAddressCorrected())
                return getGetResponse(webServerPath, phpParams);
            else
                return null;
        }
    }

    private static boolean isAddressCorrected() {

        if (HTTP_ADDRESS.contains("http://")) {
            HTTP_ADDRESS = HTTP_ADDRESS.replace("http://", "https://");
            return true;
        }

        if (HTTP_ADDRESS.contains("simpleminecraft.ru")) {
            HTTP_ADDRESS = HTTP_ADDRESS.replace("simpleminecraft.ru", "simpleminecraft.net");
            return true;
        }

        return false;
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null)
            stringBuffer.append(line);

        inputStream.close();
        bufferedReader.close();

        return stringBuffer.toString();
    }

}
