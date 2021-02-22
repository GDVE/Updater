package ru.simplemc.updater.gui.utils;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsUtils {

    public static void drawString(Graphics2D graphics2D, float fontSize, String fontName, String color, String text, int x, int y) {

        graphics2D.setColor(Color.decode("#" + color));
        graphics2D.setFont(ResourcesUtils.getFont(fontName, fontSize));

        if (OSUtils.isLinux())
            x = x - Settings.FRAME_SHADOW_SIZE;

        graphics2D.drawString(text, x, y);
    }

    public static void drawCenteredString(Graphics2D graphics2D, Rectangle rect, float fontSize, String fontName, String color, String text) {

        Font font = ResourcesUtils.getFont(fontName, fontSize);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        if (OSUtils.isLinux())
            x = x - Settings.FRAME_SHADOW_SIZE;

        graphics2D.setColor(Color.decode("#" + color));
        graphics2D.setFont(font);
        graphics2D.drawString(text, x, y);
    }

    public static void drawBackground(Graphics2D graphics2D, JPanel panel) {

        BufferedImage bufferedImage = ResourcesUtils.getBufferedImage("backgrounds/background_" + Settings.BACKGROUND_IMAGE + ".png");

        if (OSUtils.isLinux())
            graphics2D.drawImage(bufferedImage, 0, 0, Settings.FRAME_WIDTH, Settings.FRAME_HEIGHT, panel);
        else
            graphics2D.drawImage(bufferedImage, Settings.FRAME_SHADOW_SIZE, Settings.FRAME_SHADOW_SIZE, Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE * 2, Settings.FRAME_HEIGHT - Settings.FRAME_SHADOW_SIZE * 2, panel);
    }

}
