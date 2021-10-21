package ru.simplemc.updater.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.simplemc.updater.Environment;
import ru.simplemc.updater.service.logger.SimpleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpService {

    @Getter
    private final ObjectMapper mapper = new ObjectMapper();
    @Getter
    private final SimpleLogger logger = new SimpleLogger(HttpService.class);

    private String workingProtocol = "https://";

    private void changeWorkingProtocol() {
        workingProtocol = "http://";
        logger.info("Working protocol changed to: " + workingProtocol);
    }

    private boolean isWorkingProtocolSecure() {
        return workingProtocol.equals("https://");
    }

    public HttpURLConnection createConnection(String url) throws IOException {
        return createConnection(new URL(workingProtocol + url));
    }

    public HttpURLConnection createConnection(URL url) throws IOException {
        logger.info("Create connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", Environment.HTTP_USER_AGENT);
        return connection;
    }

    public String performPostRequest(String url, Object object) throws IOException {
        try {
            return performPostRequest(new URL(workingProtocol + url), mapper.writeValueAsString(object));
        } catch (IOException e) {
            if (isWorkingProtocolSecure()) {
                changeWorkingProtocol();
                return performPostRequest(url, object);
            }

            throw e;
        }
    }

    private String performPostRequest(URL url, String data) throws IOException {

        HttpURLConnection connection = this.createConnection(url);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        logger.info("Writing POST data to " + url + ": " + data);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(dataBytes);
        }

        logger.info("Reading data from " + url);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            logger.info("Successful read, server response was " + connection.getResponseCode());
            logger.info("Response: " + result);
            return result.toString();

        } catch (IOException e) {
            logger.error("Request failed:", e);
            throw e;
        }
    }
}
