package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.gui.utils.GraphicsUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import javax.swing.*;
import java.awt.*;

public class StartupPane extends JPanel {

    private String status;
    private String description;

    public StartupPane() {

        status = "ПОЧТИ ГОТОВО";
        description = "Запускаю лаунчер...";

        ImageIcon imageIcon = ResourcesUtils.getImageIcon("animations/success.gif");
        JLabel loadingIconLabel = new JLabel();
        loadingIconLabel.setIcon(imageIcon);
        loadingIconLabel.setBounds(Settings.FRAME_WIDTH / 2 - imageIcon.getIconWidth() / 2, 64, 64, 64);
        imageIcon.setImageObserver(loadingIconLabel);

        add(loadingIconLabel);
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GraphicsUtils.drawBackground(graphics2D, this);
        GraphicsUtils.drawCenteredString(graphics2D, new Rectangle(20, 178, Settings.FRAME_WIDTH - 30 - Settings.FRAME_SHADOW_SIZE * 2, 20), 22, "FSElliotPro", "ffffff", status);
        GraphicsUtils.drawCenteredString(graphics2D, new Rectangle(20, 208, Settings.FRAME_WIDTH - 30 - Settings.FRAME_SHADOW_SIZE * 2, 20), 14, "FSElliotPro-Bold", "e7e7e6", description);
    }

    public void setStatusAndDescription(String statusValue, String descriptionValue) {
        status = statusValue;
        description = descriptionValue;
        repaint();
    }
}
