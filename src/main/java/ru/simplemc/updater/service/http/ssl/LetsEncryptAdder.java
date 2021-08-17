package ru.simplemc.updater.service.http.ssl;

import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LetsEncryptAdder {

    public static void addLetsEncryptCertificate() throws Exception {
        InputStream cert = LetsEncryptAdder.class.getResourceAsStream("/assets/letsencryptroot/lets-encrypt-x3-cross-signed.der");

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        Path ksPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
        keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = new BufferedInputStream(Objects.requireNonNull(cert));
        Certificate crt = cf.generateCertificate(caInput);
        keyStore.setCertificateEntry("lets-encrypt-x3-cross-signed", crt);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
    }

    public static void doStuff() throws Exception {
        String version = System.getProperty("java.version");
        Pattern p = Pattern.compile("^(\\d+\\.\\d+).*?_(\\d+).*");
        Matcher matcher = p.matcher(version);
        String majorVersion;
        int minorVersion;
        if (matcher.matches()) {
            majorVersion = matcher.group(1);
            minorVersion = Integer.parseInt(matcher.group(2));
        } else {
            majorVersion = "1.7";
            minorVersion = 110;
        }

        switch (majorVersion) {
            case "1.7":
                if (minorVersion >= 111) {
                    return;
                }
                break;
            case "1.8":
                if (minorVersion >= 101) {
                    return;
                }
                break;
        }
        LetsEncryptAdder.addLetsEncryptCertificate();
    }
}
