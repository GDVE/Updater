package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.gui.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;

public class StartupPane extends JPanel {

    private String status;
    private String description;

    public StartupPane() {
        status = "ВСЕ ГОТОВО!";
        description = "Запускаю лаунчер...";
        /*
        ImageIcon imageIcon = ResourcesUtils.getImageIcon("animations/success.gif");
        JLabel loadingIconLabel = new JLabel();
        loadingIconLabel.setIcon(imageIcon);
        loadingIconLabel.setBounds(Settings.FRAME_WIDTH / 2 - imageIcon.getIconWidth() / 2, 64, 64, 64);
        imageIcon.setImageObserver(loadingIconLabel);
        add(loadingIconLabel);
         */
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GraphicsUtils.drawBackground(graphics2D, this);
        GraphicsUtils.drawString(graphics2D, 23, "FSElliotPro-Heavy", "ffffff", status, 40, 76);
        GraphicsUtils.drawString(graphics2D, 16, "FSElliotPro-Bold", "e7e7e6", description, 40, 102);
    }

    public void setStatusAndDescription(String statusValue, String descriptionValue) {
        status = statusValue.toUpperCase();
        description = descriptionValue;
        repaint();
    }
}
