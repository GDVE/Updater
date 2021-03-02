package ru.simplemc.updater.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplemc.updater.Settings;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

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

            HttpURLConnection connection = openConnection("POST", path);
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
            if (getProtocol().equals("https") && disableSSLCertificatesChecks()) {
                return post(path, params);
            } else if (switchToHTTPS()) {
                return post(path, params);
            } else
                throw e;
        }
    }

    /**
     * @param path - путь до исполняемого файла на хосте
     * @return - возвращает HttpURLConnection для дальнейшей обработки
     * @throws Exception - выбрасывает в случае проблем с соединением
     */
    public static HttpURLConnection openConnection(String method, String path) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(Settings.HTTP_ADDRESS + path).openConnection();
        connection.setRequestProperty("User-Agent", Settings.HTTP_USER_AGENT);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);

        if (method.equals("POST")) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
        }

        return connection;
    }

    /**
     * @return Возвращает текущий HTTP протокол с которым работает лаунчер на данный момент
     */
    private static String getProtocol() {
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

    /**
     * Необходима для предотвращения двойного отключения
     */
    private static boolean sslCheckingIsDisabled = false;

    /**
     * Выключает проверку SSL сертификатов у определенной категории игроков,
     * у которых все совсем плохо с работспособностью HTTP.
     */
    private static boolean disableSSLCertificatesChecks() {

        if (sslCheckingIsDisabled) {
            return false;
        }

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ignored) {
        }

        sslCheckingIsDisabled = true;
        return true;
    }

}
