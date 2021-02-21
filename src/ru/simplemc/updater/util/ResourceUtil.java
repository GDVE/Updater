package ru.simplemc.updater.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {

    private static final Map<String, BufferedImage> bufferedImagesCache = new HashMap<>();
    private static final Map<String, ImageIcon> iconImagesCache = new HashMap<>();
    private static final Map<String, Font> FONTS_LOADED = new HashMap<>();

    public static Font getFont(String fontName, float size) {

        Font font = Font.getFont("Arial");

        try {

            font = FONTS_LOADED.getOrDefault(
                    fontName,
                    Font.createFont(0, ResourceUtil.class.getResourceAsStream("/assets/fonts/" + fontName + ".ttf")))
                    .deriveFont(size);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        if (!FONTS_LOADED.containsKey(fontName))
            FONTS_LOADED.put(fontName, font);

        return font;
    }

    public static ImageIcon getImageIcon(String path) {

        path = "/assets/images/" + path;

        if (iconImagesCache.containsKey(path))
            return iconImagesCache.get(path);
        else {

            ImageIcon imageIcon = new ImageIcon(new BufferedImage(1, 1, 2));

            try {
                imageIcon = new ImageIcon(ResourceUtil.class.getResource(path));
            } catch (Exception e) {
                System.out.println("Неудалось загрузить иконку:");
                e.printStackTrace();
            }

            iconImagesCache.put(path, imageIcon);
            return imageIcon;
        }
    }

    public static BufferedImage getBufferedImage(String path) {

        path = "/assets/images/" + path;

        if (bufferedImagesCache.containsKey(path))
            return bufferedImagesCache.get(path);
        else {

            BufferedImage bufferedImage = new BufferedImage(1, 1, 2);

            try {
                bufferedImage = ImageIO.read(ResourceUtil.class.getResource(path));
            } catch (Exception e) {
                System.out.println("Неудалось загрузить изображение:");
                e.printStackTrace();
            }

            bufferedImagesCache.put(path, bufferedImage);
            return bufferedImage;
        }
    }

}
