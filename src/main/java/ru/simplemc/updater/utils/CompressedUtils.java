package ru.simplemc.updater.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressedUtils {

    public static void unZipArchive(File archiveFile, File extractDirectory) {
        try {
            FileInputStream fileInputStream = new FileInputStream(archiveFile);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                extractFromZip(zipEntry, zipInputStream, extractDirectory);
            }

            zipInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void extractFromZip(ZipEntry zipEntry, ZipInputStream zipInputStream, File extractDirectory) throws Exception {
        if (zipEntry.isDirectory()) {
            return;
        }

        String zipEntryName = slashToFileSeparator(zipEntry.getName());
        String zipEntryDirPath;

        if (zipEntryName.lastIndexOf(File.separator) != -1)
            zipEntryDirPath = zipEntryName.substring(0, zipEntryName.lastIndexOf(File.separator));
        else
            zipEntryDirPath = "";

        Files.createDirectories(Paths.get(extractDirectory + File.separator + zipEntryDirPath));
        FileOutputStream fileOutputStream = new FileOutputStream(extractDirectory + File.separator + zipEntryName);
        byte[] buffer = new byte[1024];

        while (true) {
            int length = zipInputStream.read(buffer);
            if (length < 0) break;
            fileOutputStream.write(buffer, 0, length);
        }

        fileOutputStream.close();
    }

    private static String slashToFileSeparator(String source) {
        char[] chars = new char[source.length()];

        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '/') chars[i] = File.separatorChar;
            else chars[i] = source.charAt(i);
        }

        return new String(chars);
    }
}
