package ru.simplemc.updater.service.downloader.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class FileInfo {

    @Getter
    @JsonProperty
    private String path;

    @Getter
    @JsonProperty
    private String name;

    @Getter
    @JsonProperty
    private String url;

    @Getter
    @JsonProperty
    private String md5;

    @Getter
    @JsonProperty
    private long size;

}
