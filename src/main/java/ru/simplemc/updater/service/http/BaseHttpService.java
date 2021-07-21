package ru.simplemc.updater.service.http;

import org.apache.commons.io.IOUtils;
import ru.simplemc.updater.Environment;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BaseHttpService {

    private void printLog(String message) {
        if (ProgramUtils.isDebugMode()) System.out.println("[" + getClass().getSimpleName() + "] " + message);
    }

    public HttpURLConnection createConnection(URL url) throws IOException {
        Objects.requireNonNull(url);
        printLog("Open connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", Environment.HTTP_USER_AGENT);
        return connection;
    }

    public String performPostRequest(URL url, String post, String contentType) throws IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(post);
        Objects.requireNonNull(contentType);

        HttpURLConnection connection = this.createConnection(url);
        byte[] postAsBytes = post.getBytes(StandardCharsets.UTF_8);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(postAsBytes.length));
        connection.setDoOutput(true);

        printLog("Writing POST data to " + url + ": " + post);
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            outputStream.write(postAsBytes);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        printLog("Reading data from " + url);
        InputStream inputStream = null;

        String result;
        try {
            try {
                inputStream = connection.getInputStream();
                result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                printLog("Successful read, server response was " + connection.getResponseCode());
                printLog("Response: " + result);
                return result;
            } catch (IOException e) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    printLog("Request failed");
                    e.printStackTrace();
                    throw e;
                }
                printLog("Reading error page from " + url);
                result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                printLog("Successful read, server response was " + connection.getResponseCode());
                printLog("Response: " + result);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }
}
