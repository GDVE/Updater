package ru.simplemc.updater.utils;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import ru.simplemc.updater.Updater;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressedUtils {

    public static void extractArchive(File archive, File directoryTo) throws IOException {

        try {
            if (archive.getName().endsWith(".zip")) {
                CompressedUtils.extractZipArchive(archive, directoryTo);
                return;
            }
        } catch (IOException e) {
            Updater.getLogger().error("Failed to extract " + archive.getName() +
                    " archive with legacy method:", e);
        }

        Archiver archiver = ArchiverFactory.createArchiver(archive);
        archiver.extract(archive, directoryTo);
    }

    private static void extractZipArchive(File archive, File directoryTo) throws IOException {
        try (ZipInputStream inputStream = new ZipInputStream(new BufferedInputStream(
                new FileInputStream(archive)))) {

            ZipEntry entry;

            while ((entry = inputStream.getNextEntry()) != null) {
                extractZipEntry(entry, inputStream, directoryTo);
            }
        }
    }

    private static void extractZipEntry(ZipEntry entry, ZipInputStream inputStream, File directoryTo)
            throws IOException {

        if (entry.isDirectory()) {
            return;
        }

        String entryName = slashToFileSeparator(entry.getName());
        String entryParent;

        if (entryName.lastIndexOf(File.separator) != -1)
            entryParent = entryName.substring(0, entryName.lastIndexOf(File.separator));
        else
            entryParent = "";

        Files.createDirectories(Paths.get(directoryTo + File.separator + entryParent));
        FileOutputStream fileOutputStream = new FileOutputStream(directoryTo + File.separator + entryName);
        byte[] buffer = new byte[1024];

        while (true) {
            int length = inputStream.read(buffer);
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
