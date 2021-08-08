package ru.simplemc.updater.gui.utils;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsUtils {

    public static void drawString(Graphics2D graphics2D, float fontSize, String fontName, String color, String text, int x, int y) {

        graphics2D.setColor(Color.decode("#" + color));
        graphics2D.setFont(ResourcesUtils.getOrCreateFont(fontName, fontSize));

        if (OSUtils.isLinux() || OSUtils.isMacOS()) {
            x = x - Environment.FRAME_SHADOW_SIZE;
            y = y - Environment.FRAME_SHADOW_SIZE;
        }

        graphics2D.drawString(text, x, y);
    }

    public static void drawBackground(Graphics2D graphics2D, JPanel panel) {

        BufferedImage bufferedImage = ResourcesUtils.getOrCreateBufferedImage("backgrounds/background_" + Environment.BACKGROUND_IMAGE + ".png");

        if (OSUtils.isLinux() || OSUtils.isMacOS())
            graphics2D.drawImage(bufferedImage, 0, 0, Environment.FRAME_WIDTH, Environment.FRAME_HEIGHT, panel);
        else
            graphics2D.drawImage(bufferedImage, Environment.FRAME_SHADOW_SIZE, Environment.FRAME_SHADOW_SIZE, Environment.FRAME_WIDTH - Environment.FRAME_SHADOW_SIZE * 2, Environment.FRAME_HEIGHT - Environment.FRAME_SHADOW_SIZE * 2, panel);
    }

}
