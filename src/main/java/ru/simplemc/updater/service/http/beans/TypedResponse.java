package ru.simplemc.updater.service.http.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TypedResponse {

    @JsonProperty
    private String type;
    @JsonProperty
    private String title;
    @JsonProperty
    private String message;

}
