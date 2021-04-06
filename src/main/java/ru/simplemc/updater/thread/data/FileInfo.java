package ru.simplemc.updater.thread.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class FileInfo {

    @JsonProperty
    @Getter
    private String path;
    @JsonProperty
    @Getter
    private String name;
    @JsonProperty
    @Getter
    private String url;
    @JsonProperty
    @Getter
    private String md5;
    @JsonProperty
    @Getter
    private long size;

}
