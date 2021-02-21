package ru.simplemc.updater.data;

import ru.simplemc.updater.util.MessageUtil;
import ru.simplemc.updater.util.PathUtil;

import java.io.*;
import java.util.Collection;
import java.util.Properties;

public class Config {

    private final File file;
    private final Properties properties;
    private final boolean withCreate;

    public Config(String name, boolean needCreate) {

        file = new File(PathUtil.getStorageDirectoryPath(), name + ".conf");
        properties = new Properties();
        withCreate = needCreate;

        init();
    }

    public void init() {
        if (file.exists())
            try {
                load();
            } catch (IOException e) {
                MessageUtil.printException("Неудалось прочитать файл конфигурации!", "Возможно файл заблокирован сторонней программой или повреждён:\n\nПуть к проблемному файлу:\n" + file.toString(), e);
            }
        else if (withCreate)
            try {
                write();
            } catch (IOException e) {
                MessageUtil.printException("Неудалось создать файл конфигурации!", "Возможно создание файла было заблокировано сторонней программой или ваша файловая система неисправна.\n\nПусть, по которому была попытка создания файла:\n" + file.toString(), e);
            }
    }

    public void load() throws IOException {
        InputStream inputStream = new FileInputStream(file);
        properties.load(inputStream);
        inputStream.close();
    }

    public void write() throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        properties.store(outputStream, "SimpleMinecraft.Ru Updater 2.0 by Goodvise.");
        outputStream.close();
    }

    public void save() {
        try {
            write();
        } catch (IOException e) {
            MessageUtil.printException("Неудалось сохранить файл конфигурации!", "Возможно сохранение файла было заблокировано сторонней программой или ваша файловая система неисправна.\n\nПусть к проблемному файлу:\n" + file.toString(), e);
        }
    }

    public void setProperty(String key, Object value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public int getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public String getString(String key) {
        return String.valueOf(properties.getProperty(key));
    }

    public File getFile(String key) {
        return new File(properties.getProperty(key));
    }

    public Collection<Object> getValues() {
        return properties.values();
    }

    public boolean hasValue(String key) {
        return properties.getProperty(key) != null;
    }

    public void remove(String key) {
        properties.remove(key);
    }
}
