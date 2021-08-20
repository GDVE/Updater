package ru.simplemc.updater.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpServiceManager {

    @Getter
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final BaseHttpService httpService = new BaseHttpService();
    private static String protocol = "https://";
    private static boolean trustManagerChanged = false;

    private static boolean changeProtocol() {
        if (protocol.equals("https://")) {
            protocol = "http://";
            return true;
        }

        return false;
    }

    private static boolean changeTrustManager() {
        if (trustManagerChanged) {
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
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        trustManagerChanged = true;
        return true;
    }

    public static HttpURLConnection createConnection(String url) throws IOException {
        try {
            return httpService.createConnection(new URL(protocol + url));
        } catch (IOException e) {
            if (changeTrustManager() || changeProtocol()) {
                return createConnection(url);
            } else {
                throw e;
            }
        }
    }

    public static String performPostRequest(String url, Object object) throws IOException {
        try {
            return httpService.performPostRequest(new URL(protocol + url), mapper.writeValueAsString(object),
                    "application/json");
        } catch (IOException e) {
            if (changeTrustManager() || changeProtocol()) {
                return performPostRequest(url, object);
            } else {
                throw e;
            }
        }
    }

}
