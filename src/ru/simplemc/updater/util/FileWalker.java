package ru.simplemc.updater.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileWalker {

    private final List<File> fileList = new ArrayList<>();

    public FileWalker(File rootDirectory) {
        this.walk(rootDirectory);
    }

    private void walk(File root) {

        File[] list = root.listFiles();

        if (list == null)
            return;

        for (File file : list) {
            if (file.isDirectory()) {
                walk(file);
            } else {
                fileList.add(file);
            }
        }
    }

    public List<File> getFiles() {
        return fileList;
    }

    public String getFilesMapHash() {

        StringBuilder rawFilesData = new StringBuilder();

        for (File file : fileList)
            rawFilesData.append(file.getParentFile().getName()).append(File.separator).append(file.getName());

        return CryptUtil.getSha1(rawFilesData.toString());
    }
}
