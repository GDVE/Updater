package ru.simplemc.updater.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcesUtils {

    private static final Map<String, BufferedImage> BUFFERED_IMAGES_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Font> FONTS_LOADED = new ConcurrentHashMap<>();

    public static Font getOrCreateFont(String fontName, float size) {

        Font font = Font.getFont("Arial");

        try {
            font = FONTS_LOADED.getOrDefault(
                    fontName,
                    Font.createFont(0, Objects.requireNonNull(
                            ResourcesUtils.class.getResourceAsStream("/assets/fonts/" + fontName + ".ttf"))))
                    .deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        if (!FONTS_LOADED.containsKey(fontName)) FONTS_LOADED.put(fontName, font);
        return font;
    }

    public static BufferedImage getOrCreateBufferedImage(String path) {

        path = "/assets/images/" + path;

        if (BUFFERED_IMAGES_CACHE.containsKey(path))
            return BUFFERED_IMAGES_CACHE.get(path);
        else {

            BufferedImage bufferedImage = new BufferedImage(1, 1, 2);

            try {
                bufferedImage = ImageIO.read(Objects.requireNonNull(ResourcesUtils.class.getResource(path)));
            } catch (Exception e) {
                System.out.println("Неудалось загрузить изображение:");
                e.printStackTrace();
            }

            BUFFERED_IMAGES_CACHE.put(path, bufferedImage);
            return bufferedImage;
        }
    }
}
