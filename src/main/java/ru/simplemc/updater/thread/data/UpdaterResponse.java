package ru.simplemc.updater.thread.data;

import ru.simplemc.updater.downloader.file.DownloaderFile;
import ru.simplemc.updater.downloader.file.DownloaderRuntimeArchiveFile;

import java.util.ArrayList;
import java.util.List;

public class UpdaterResponse extends ArrayList<FileInfo> {

    public List<DownloaderFile> getDownloaderFiles() {

        List<DownloaderFile> files = new ArrayList<>();

        for (FileInfo fileInfo : this) {
            if (fileInfo.getPath().contains("runtime"))
                files.add(new DownloaderRuntimeArchiveFile(fileInfo));
            else
                files.add(new DownloaderFile(fileInfo));
        }

        return files;
    }

}
    


