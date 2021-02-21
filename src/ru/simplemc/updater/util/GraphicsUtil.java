package ru.simplemc.updater.util;

import ru.simplemc.updater.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsUtil {

    public static void drawString(Graphics2D graphics2D, float fontSize, String fontName, String color, String text, int x, int y) {

        graphics2D.setColor(Color.decode("#" + color));
        graphics2D.setFont(ResourceUtil.getFont(fontName, fontSize));

        if (SystemUtil.isUnix())
            x = x - Settings.FRAME_SHADOW_SIZE;

        graphics2D.drawString(text, x, y);
    }

    public static void drawCenteredString(Graphics2D graphics2D, Rectangle rect, float fontSize, String fontName, String color, String text) {

        Font font = ResourceUtil.getFont(fontName, fontSize);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        if (SystemUtil.isUnix())
            x = x - Settings.FRAME_SHADOW_SIZE;

        graphics2D.setColor(Color.decode("#" + color));
        graphics2D.setFont(font);
        graphics2D.drawString(text, x, y);
    }

    public static void drawBackground(Graphics2D graphics2D, JPanel panel) {

        BufferedImage bufferedImage = ResourceUtil.getBufferedImage("backgrounds/background_" + Settings.BACKGROUND_IMAGE + ".png");

        if (SystemUtil.isUnix())
            graphics2D.drawImage(bufferedImage, 0, 0, Settings.FRAME_WIDTH, Settings.FRAME_HEIGHT, panel);
        else
            graphics2D.drawImage(bufferedImage, Settings.FRAME_SHADOW_SIZE, Settings.FRAME_SHADOW_SIZE, Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE * 2, Settings.FRAME_HEIGHT - Settings.FRAME_SHADOW_SIZE * 2, panel);
    }

}
