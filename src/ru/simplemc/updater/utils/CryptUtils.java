package ru.simplemc.updater.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {

    /**
     * Возвращает SHA1 из переданной строки
     *
     * @param string - строка для хеширования
     * @return - хешированная строка
     */
    public static String sha1(String string) {

        String sha1String = null;

        try {

            StringBuilder stringBuilder = new StringBuilder();

            for (byte digestByte : MessageDigest.getInstance("SHA1").digest(string.getBytes())) {
                stringBuilder.append(Integer.toString((digestByte & 0xff) + 0x100, 16).substring(1));
            }

            sha1String = stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sha1String;
    }

    /**
     * Возвращает MD5 из переданной строки
     *
     * @param string - строка для хеширования
     * @return - хешированная строка
     */
    public static String md5(String string) {

        String md5String = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(), 0, string.length());
            md5String = new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5String;
    }

    /**
     * Возвращает MD5 файла
     *
     * @param path - путь до нужного файла
     * @return - хеш-сумма файла
     */
    public static String md5(Path path) {
        return md5(path.toFile());
    }

    /**
     * Возвращает MD5 файла
     *
     * @param file - непосредственно сам файл
     * @return - хеш-сумма файла
     */
    public static String md5(File file) {

        StringBuilder stringBuilder = new StringBuilder();

        if (file.exists() && !file.isDirectory()) {
            try {

                InputStream fileInputStream = new FileInputStream(file);
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int byteNumber;

                do {

                    byteNumber = fileInputStream.read(buffer);

                    if (byteNumber > 0)
                        messageDigest.update(buffer, 0, byteNumber);

                } while (byteNumber != -1);

                fileInputStream.close();

                for (byte digestByte : messageDigest.digest()) {
                    stringBuilder.append(Integer.toString((digestByte & 0xff) + 0x100, 16).substring(1));
                }

            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

}
