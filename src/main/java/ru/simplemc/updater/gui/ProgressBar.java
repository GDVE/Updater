package ru.simplemc.updater.gui;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.utils.OSUtils;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    public ProgressBar(int height) {

        setMinimum(0);
        setMaximum(100);
        setString("0%");
        setStringPainted(false);
        setBorderPainted(false);
        setForeground(Color.decode("#92d246"));
        setBackground(Color.decode("#e7e7e6"));

        if (OSUtils.isLinux() || OSUtils.isMacOS())
            setBounds(new Rectangle(40 - Environment.FRAME_SHADOW_SIZE, Environment.FRAME_HEIGHT - Environment.FRAME_SHADOW_SIZE - 32 - height, Environment.FRAME_WIDTH - Environment.FRAME_SHADOW_SIZE - 40 * 2, height));
        else
            setBounds(new Rectangle(40, Environment.FRAME_HEIGHT - Environment.FRAME_SHADOW_SIZE - 32 - height, Environment.FRAME_WIDTH - Environment.FRAME_SHADOW_SIZE - 40 * 2, height));

        setVisible(true);
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }
}
