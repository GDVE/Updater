package ru.simplemc.updater.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesScanner {

    private static List<File> scan(File path, List<File> fileList) {

        if (path.isDirectory()) {

            File[] files = path.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scan(file, fileList);
                    } else
                        fileList.add(file);
                }
            }
        } else {
            fileList.add(path);
        }

        return fileList;
    }

    public static List<File> walk(File path) {
        List<File> files = new ArrayList<>();
        return scan(path, files);
    }

}
