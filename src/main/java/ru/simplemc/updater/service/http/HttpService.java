package ru.simplemc.updater.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.simplemc.updater.Environment;
import ru.simplemc.updater.Updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpService {

    @Getter
    private final ObjectMapper mapper = new ObjectMapper();

    private String workingProtocol = "https://";
    private String currentDomain = ".ru";

    private void changeWorkingProtocol(String protocol) {
        workingProtocol = protocol + "://";
    }

    private void changeWorkingDomain(String domain) {
        currentDomain = "." + domain;
    }

    private boolean isWorkingProtocolChanged() {
        return workingProtocol.equals("http://");
    }

    private boolean isWorkingDomainChanged() {
        return currentDomain.equals(".net");
    }

    public HttpURLConnection createConnection(String url) throws IOException {
        return createConnection(new URL(workingProtocol + url.replace(".ru/", currentDomain + "/")));
    }

    public HttpURLConnection createConnection(URL url) throws IOException {
        Updater.getLogger().info("Create connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", Environment.HTTP_USER_AGENT);
        return connection;
    }

    public String performPostRequest(String path, Object object) throws IOException {

        URL url = new URL(workingProtocol + path.replace(".ru/", currentDomain + "/"));

        try {
            return performPostRequest(url, mapper.writeValueAsString(object));
        } catch (IOException e) {
            if (isWorkingProtocolChanged() && isWorkingDomainChanged()) {
                Updater.getLogger().info("Reset domain and protocol to default...");
                changeWorkingProtocol("https");
                changeWorkingDomain("ru");
                throw e;
            } else if (!isWorkingProtocolChanged()) {
                Updater.getLogger().info("Change working protocol to: HTTP");
                changeWorkingProtocol("http");
                return performPostRequest(path, object);
            }

            Updater.getLogger().info("Change working protocol to: HTTPS");
            Updater.getLogger().info("Change working domain to: NET");
            changeWorkingProtocol("https");
            changeWorkingDomain("net");

            return performPostRequest(path, object);
        }
    }

    private String performPostRequest(URL url, String data) throws IOException {

        HttpURLConnection connection = this.createConnection(url);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        Updater.getLogger().info("Writing POST data to " + url + ": " + data);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(dataBytes);
        }

        Updater.getLogger().info("Reading data from " + url);

        try {

            if (connection.getResponseCode() != 200) {
                throw new IOException("Server response is not OK.");
            }

            String result = readBuffer(connection.getInputStream());
            Updater.getLogger().info("Successful read, server response was " + connection.getResponseCode());
            Updater.getLogger().info("Response: " + result);
            return result;
        } catch (IOException e) {
            String result = readBuffer(connection.getErrorStream() == null ? connection.getInputStream() : connection.getErrorStream());
            Updater.getLogger().info("Request failed, server response was " + connection.getResponseCode());
            Updater.getLogger().info("Response: " + result);
            throw e;
        }
    }

    private String readBuffer(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        }
    }
}
