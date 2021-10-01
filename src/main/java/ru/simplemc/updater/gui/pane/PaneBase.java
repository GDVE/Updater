package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.Environment;

import javax.swing.*;
import java.awt.*;

public class PaneBase extends JPanel {

    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(90, 80,
                Environment.BACKGROUND_COLOR, getWidth(), getHeight(), Environment.BACKGROUND_COLOR_2
        );

        graphics2D.setPaint(gp);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
    }
}
