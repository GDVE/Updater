package ru.simplemc.updater.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config extends Properties {

    private final Path path;
    private final Map<String, String> defaultValues = new HashMap<>();

    public Config(Path path, boolean withCreate) throws IOException {

        this.path = path;

        if (Files.exists(path)) {
            loadFromFile();
        } else if (withCreate)
            store();
    }

    public void addDefaultValue(String key, String value) {
        this.defaultValues.put(key, value);
    }

    private void loadFromFile() throws IOException {
        FileInputStream inputStream = new FileInputStream(path.toFile());
        this.load(inputStream);
        inputStream.close();
    }

    public void store() throws IOException {

        Path parent = path.getParent();

        if (!Files.exists(parent))
            Files.createDirectory(parent);

        FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
        this.store(fileOutputStream, "SimpleMinecraft.Ru 2021 (developed by Goodvise)");
        fileOutputStream.close();
    }

    public void storeSilent() {
        try {
            this.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProperty(String key) {

        String property = super.getProperty(key);

        if (property == null && defaultValues.containsKey(key)) {
            property = defaultValues.get(key);
            this.setProperty(key, property);
        }

        return property;
    }

    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }

    public void setProperty(String key, boolean value) {
        setProperty(key, String.valueOf(value));
    }

    public void setProperty(String key, double value) {
        setProperty(key, String.valueOf(value));
    }

    public void setProperty(String key, int value) {
        setProperty(key, String.valueOf(value));
    }

    public boolean getPropertyBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key, "false"));
    }

    public double getPropertyDouble(String key) {
        return Double.parseDouble(getProperty(key));
    }

    public int getPropertyInteger(String key) {
        return Integer.parseInt(getProperty(key));
    }

}
