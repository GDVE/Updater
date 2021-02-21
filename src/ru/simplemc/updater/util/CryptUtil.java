package ru.simplemc.updater.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class CryptUtil {

    public static String getMd5String(String s) {

        String hash = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            hash = new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }

    public static String getSha1(String input) {
        String hash = null;
        try {
            MessageDigest m = MessageDigest.getInstance("SHA1");
            byte[] result = m.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }

    public static String getMd5File(File file) {

        FileInputStream fis = null;
        DigestInputStream dis = null;
        BufferedInputStream bis = null;
        Formatter formatter = null;

        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DigestInputStream(bis, messagedigest);
            while (dis.read() != -1) ;
            byte abyte0[] = messagedigest.digest();
            formatter = new Formatter();
            byte abyte1[] = abyte0;
            int i = abyte1.length;
            for (int j = 0; j < i; j++) {
                byte byte0 = abyte1[j];
                formatter.format("%02x", new Object[]{Byte.valueOf(byte0)});
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            return "";
        } finally {
            try {
                assert fis != null;
                fis.close();
            } catch (Exception ignored) {
            }
            try {
                assert dis != null;
                dis.close();
            } catch (Exception ignored) {
            }
            try {
                bis.close();
            } catch (Exception ignored) {
            }
            try {
                assert formatter != null;
                formatter.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean compareFileHash(File file, String hash) {
        return getMd5File(file).equals(hash);
    }

}
