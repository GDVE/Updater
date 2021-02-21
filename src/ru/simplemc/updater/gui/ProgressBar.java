package ru.simplemc.updater.gui;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.util.ResourceUtil;
import ru.simplemc.updater.util.SystemUtil;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    public ProgressBar(int height) {

        setFont(ResourceUtil.getFont("FSElliotPro-Bold", 12));
        setMinimum(0);
        setMaximum(100);
        setString("0%");
        setStringPainted(false);
        setBorderPainted(false);
        setForeground(Color.decode("#92d246"));
        setBackground(Color.decode("#e7e7e6"));

        if (SystemUtil.isUnix())
            setBounds(new Rectangle(40 - Settings.FRAME_SHADOW_SIZE, Settings.FRAME_HEIGHT - Settings.FRAME_SHADOW_SIZE - 40 - height, Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE - 40 * 2, height));
        else
            setBounds(new Rectangle(40, Settings.FRAME_HEIGHT - Settings.FRAME_SHADOW_SIZE - 40 - height, Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE - 40 * 2, height));

        setVisible(true);
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }
}
