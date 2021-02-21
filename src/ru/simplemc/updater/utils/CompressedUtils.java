package ru.simplemc.updater.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressedUtils {

    public static void unZipArchive(File archiveFile) {
        unZipArchive(archiveFile, archiveFile.getParentFile());
    }

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

        if (zipEntry.isDirectory())
            return;

        String zipEntryName = slashToFileSeparator(zipEntry.getName());
        String zipEntryDirPath;

        if (zipEntryName.lastIndexOf(File.separator) != -1)
            zipEntryDirPath = zipEntryName.substring(0, zipEntryName.lastIndexOf(File.separator));
        else
            zipEntryDirPath = "";

        new File(extractDirectory + File.separator + zipEntryDirPath).mkdirs();

        FileOutputStream fileOutputStream = new FileOutputStream(extractDirectory + File.separator + zipEntryName);
        byte[] buffer = new byte[1024];

        while (true) {

            int length = zipInputStream.read(buffer);

            if (length < 0)
                break;

            fileOutputStream.write(buffer, 0, length);
        }

        fileOutputStream.close();
    }

    public static void decompressTarGzip(File archiveFile) {

        TarArchiveInputStream tarArchiveInputStream = null;

        try {
            tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(archiveFile))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tarArchiveInputStream != null) {

            TarArchiveEntry tarArchiveEntry;

            try {
                while ((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                    extractFromTar(tarArchiveEntry, tarArchiveInputStream, archiveFile.getParentFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                tarArchiveInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (archiveFile.delete())
                System.out.println("Remove unpacked archive: " + archiveFile);
        }
    }

    public static void extractFromTar(TarArchiveEntry tarArchiveEntry, TarArchiveInputStream tarArchiveInputStream, File extractDirectory) throws IOException {

        if (tarArchiveEntry.isDirectory())
            return;

        String zipEntryName = slashToFileSeparator(tarArchiveEntry.getName());
        String zipEntryDirPath;

        if (zipEntryName.lastIndexOf(File.separator) != -1)
            zipEntryDirPath = zipEntryName.substring(0, zipEntryName.lastIndexOf(File.separator));
        else
            zipEntryDirPath = "";

        new File(extractDirectory + File.separator + zipEntryDirPath).mkdirs();

        FileOutputStream fileOutputStream = new FileOutputStream(extractDirectory + File.separator + zipEntryName);
        byte[] buffer = new byte[1024];

        while (true) {

            int length = tarArchiveInputStream.read(buffer);

            if (length < 0)
                break;

            fileOutputStream.write(buffer, 0, length);
        }

        fileOutputStream.close();

    }

    private static String slashToFileSeparator(String source) {

        char[] chars = new char[source.length()];

        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '/')
                chars[i] = File.separatorChar;
            else
                chars[i] = source.charAt(i);
        }

        return new String(chars);
    }

}
