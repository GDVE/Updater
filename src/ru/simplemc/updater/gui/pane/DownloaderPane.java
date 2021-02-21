package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.gui.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;

public class DownloaderPane extends JPanel {

    private final ProgressBar progressBar;
    private String status;
    private String description;

    public DownloaderPane() {
        setStatusAndDescription("", "");
        add(progressBar = new ProgressBar(8));
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GraphicsUtils.drawBackground(graphics2D, this);

        GraphicsUtils.drawString(graphics2D, 23, "FSElliotPro-Heavy", "ffffff", status, 40, progressBar.isVisible() ? 68 : 76);
        GraphicsUtils.drawString(graphics2D, 16, "FSElliotPro", "e7e7e6", description, 40, progressBar.isVisible() ? 92 : 102);
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
