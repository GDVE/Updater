package ru.simplemc.updater.ui.pane;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.ui.ProgressBar;
import ru.simplemc.updater.util.GraphicsUtil;
import ru.simplemc.updater.util.ResourceUtil;
import ru.simplemc.updater.util.enums.FontFamily;

import javax.swing.*;
import java.awt.*;

public class DownloaderPane extends JPanel {

    private final ProgressBar progressBar;
    private String status;
    private String description;

    public DownloaderPane() {

        setStatusAndDescription("", "");

        ImageIcon imageIcon = ResourceUtil.getImageIcon("animations/download.gif");
        JLabel loadingIconLabel = new JLabel();
        loadingIconLabel.setIcon(imageIcon);
        loadingIconLabel.setBounds(Settings.FRAME_WIDTH / 2 - imageIcon.getIconWidth() / 2, 64, 64, 64);
        imageIcon.setImageObserver(loadingIconLabel);

        add(loadingIconLabel);

        progressBar = new ProgressBar(8);

        add(progressBar);
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GraphicsUtil.drawBackground(graphics2D, this);
        GraphicsUtil.drawString(graphics2D, 22, "FSElliotPro", "ffffff", status, 40, 180);
        GraphicsUtil.drawString(graphics2D, 14, "FSElliotPro-Bold", "e7e7e6", description, 40, 204);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setStatusAndDescription(String statusValue, String descriptionValue) {
        status = statusValue.toUpperCase();
        description = descriptionValue;
        repaint();
    }
}
