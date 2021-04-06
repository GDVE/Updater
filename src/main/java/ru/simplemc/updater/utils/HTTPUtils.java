package ru.simplemc.updater.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.simplemc.updater.Settings;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HTTPUtils {

    private static String PROTOCOL = "http";
    private static boolean SSL_CHECKING_IS_DISABLED = false;

    /**
     * @return возвращает TRUE при успешном переключении на протокол HTTPS
     */
    private static boolean switchToHTTPS() {
        if (PROTOCOL.equals("http")) {
            log("Main protocol switched to HTTPS.");
            PROTOCOL = "https";
            return true;
        }

        return false;
    }

    /**
     * Выключает проверку SSL сертификатов у определенной категории игроков,
     * у которых все совсем плохо с работспособностью HTTP.
     */
    private static boolean disableSSLCertificatesChecks() {

        if (SSL_CHECKING_IS_DISABLED) {
            return false;
        }

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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

        log("!!! SSL certificates disabled !!!");
        SSL_CHECKING_IS_DISABLED = true;
        return true;
    }

    /**
     * Красивый System.out.println
     *
     * @param message - сообщение для отправки
     */
    private static void log(String message) {
        System.out.println("[HTTPManager] " + message);
    }

    /**
     * Записываем параметры в наш запрос отрпавляем его на сервер и получем ответ
     *
     * @param url    путь до скрипта на сервере
     * @param params - параметры в формате JSON
     * @return - возвращает ответ от сервера в формате простой строки
     * @throws Exception - в случае неудачи получаем это исключение
     */
    public static String post(String domain, String url, Object params) throws Exception {
        return post(domain, url, new ObjectMapper().writeValueAsString(params));
    }

    /**
     * Записываем параметры в наш запрос отрпавляем его на сервер и получем ответ
     *
     * @param url    путь до скрипта на сервере
     * @param params - параметры в формате JSON
     * @return - возвращает ответ от сервера в формате простой строки
     * @throws Exception - в случае неудачи получаем это исключение
     */
    public static String post(String domain, String url, String params) throws Exception {
        try {
            HttpURLConnection connection = openConnection(domain, url, "POST");
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
            if (ProgramUtils.isDebugMode()) throw e;
            if (PROTOCOL.equals("https") && disableSSLCertificatesChecks()) {
                return post(domain, url, params);
            } else if (switchToHTTPS()) {
                return post(domain, url, params);
            } else {
                throw e;
            }
        }
    }

    /**
     * Открываем соединение с сервером
     *
     * @param method - метод запроса POST или GET
     * @param path   - путь до скрипта на сервере
     * @return - возвращает готовое HTTP соединение до сервера на котором работает лаунчер
     * @throws Exception - в случае неудачи получаем это исключение
     */
    public static HttpURLConnection openConnection(String domain, String path, String method) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + "://" + domain + path.replaceAll(" ", "%20")).openConnection();
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

}
