package ru.simplemc.updater.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public interface JsonConfig<T> {

    ObjectMapper mapper = new ObjectMapper();

    /**
     * Загружает конфигурацию с диска
     *
     * @param path - путь где хранится файл
     * @throws IOException            - в случае проблем в файловой системе
     * @throws IllegalAccessException - в случае проблем с доступом к полям объекта
     * @throws NoSuchFieldException   - в случае отсутствия поля в объекте
     */
    default void readFromDisk(Path path) throws IOException, IllegalAccessException, NoSuchFieldException {

        if (!Files.exists(path)) {
            return;
        }

        JsonConfig<?> jsonConfigObj = mapper.readValue(path.toFile(), this.getClass());

        for (Field jsonField : jsonConfigObj.getClass().getDeclaredFields()) {
            if (jsonField.getAnnotation(JsonProperty.class) != null) {
                jsonField.setAccessible(true);
                Field overriddenField = this.getClass().getDeclaredField(jsonField.getName());
                overriddenField.setAccessible(true);
                overriddenField.set(this, jsonField.get(jsonConfigObj));
            }
        }
    }

    /**
     * Сохраняет конфигурацию на диск
     *
     * @param obj  - объект для записи на диск
     * @param path - путь, где будет хранится объект
     * @throws IOException - в случае проблем во время записи на диск
     */
    default void writeToDisk(T obj, Path path) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), obj);
    }

    /**
     * Удаляет данные с диска
     *
     * @param path - путь к удаляемому файлу
     * @throws IOException - в случае проблемы с удалением из-за файловой системы
     */
    default void removeFromDisk(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
