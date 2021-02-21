package ru.simplemc.updater.data.json;

import org.json.simple.JSONObject;
import ru.simplemc.updater.Settings;

public class JSONFile {

    private final JSONObject jsonObject;

    public JSONFile(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getName() {
        return jsonObject.get("filename").toString();
    }

    public String getURL() {
        return Settings.HTTP_ADDRESS + jsonObject.get("path").toString() + "?" + System.currentTimeMillis();
    }

    public String getMd5Hash() {
        return jsonObject.get("hash").toString();
    }

    public long getSize() {
        return Long.parseLong(jsonObject.get("size").toString());
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
