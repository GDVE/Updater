package ru.simplemc.updater.service.http.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CheckUpdatesRequest {

    @JsonProperty
    private String systemId;
    @JsonProperty
    private String applicationFormat;

}
