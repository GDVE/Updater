package ru.simplemc.updater.service.http.beans;

import lombok.Data;
import ru.simplemc.updater.service.downloader.beans.FileInfo;

import java.util.List;

@Data
public class CheckUpdatesResponse {

    private FileInfo launcherFileInfo;
    private FileInfo updaterFileInfo;
    private List<FileInfo> runtimesFileInfos;

}

