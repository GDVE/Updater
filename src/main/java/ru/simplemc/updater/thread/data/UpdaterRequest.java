package ru.simplemc.updater.thread.data;

import lombok.Data;

@Data
public class UpdaterRequest {
    private String systemId;
    private String applicationFormat;
}
