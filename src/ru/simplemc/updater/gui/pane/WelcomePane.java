package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.gui.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;

public class WelcomePane extends JPanel {

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GraphicsUtils.drawBackground(graphics2D, this);
        GraphicsUtils.drawString(graphics2D, 23, "FSElliotPro-Heavy", "ffffff", "ДОБРО ПОЖАЛОВАТЬ!", 40, 76);
        GraphicsUtils.drawString(graphics2D, 16, "FSElliotPro", "e7e7e6", "Поиск обновлений...", 40, 102);
    }
}
