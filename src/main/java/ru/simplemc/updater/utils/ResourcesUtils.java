package ru.simplemc.updater.utils;

import ru.simplemc.updater.Updater;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcesUtils {

    private static final Map<String, BufferedImage> BUFFERED_IMAGES_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Font> FONTS_CACHE = new ConcurrentHashMap<>();

    public static Font getOrCreateFont(String name, float size) {

        if (FONTS_CACHE.containsKey(name)) {
            return FONTS_CACHE.get(name);
        }

        Font font = findRegisteredFonts(name, size);
        FONTS_CACHE.put(name, font);
        return font;
    }

    public static BufferedImage getOrCreateBufferedImage(String name) {

        if (BUFFERED_IMAGES_CACHE.containsKey(name)) {
            return BUFFERED_IMAGES_CACHE.get(name);
        }

        BufferedImage image = new BufferedImage(1, 1, 2);

        try (InputStream inputStream = getResourceInputStream("images/" + name)) {
            image = ImageIO.read(inputStream);
        } catch (Throwable e) {
            Updater.getLogger().error("Failed to image: ", e);
        }

        BUFFERED_IMAGES_CACHE.put(name, image);
        return image;
    }

    private static Font findRegisteredFonts(String name, float size) {

        try (InputStream inputStream = getResourceInputStream("fonts/" + name + ".ttf")) {
            return Font.createFont(0, inputStream).deriveFont(size);
        } catch (Throwable e) {

            Updater.getLogger().error("Failed to loading font: ", e);

            Updater.getLogger().info("Try to find system font with name: " + name);
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font[] fonts = graphicsEnvironment.getAllFonts();

            for (Font font : fonts) {
                if (font.getName().equals(name)) {
                    return font.deriveFont(size);
                }
            }

            if (fonts.length > 0) {

                if (name.contains("-")) {
                    name = name.replace(name.split("-")[0], "Arial")
                            .replace("-", " ").replace("Heavy", "Black");
                }

                for (Font font : fonts) {
                    if (font.getName().startsWith(name)) {
                        Updater.getLogger().info("Select default font: " + font.getName());
                        return font.deriveFont(size);
                    }
                }
            }
        }

        return new Font(null, Font.PLAIN, (int) size);
    }

    private static InputStream getResourceInputStream(String path) {
        return ResourcesUtils.class.getResourceAsStream("/assets/" + path);
    }
}
