package ru.simplemc.updater.gui;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.gui.pane.PaneDownloader;
import ru.simplemc.updater.gui.pane.PaneTextStatus;
import ru.simplemc.updater.utils.OSUtils;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    public ProgressBar(PaneDownloader parent) {
        setMinimum(0);
        setMaximum(100);
        setString("0%");
        setStringPainted(false);
        setBorderPainted(false);
        setForeground(Environment.PROGRESS_BAR_COLOR_FOREGROUND);
        setBackground(Environment.PROGRESS_BAR_COLOR_BACKGROUND);
        setBounds(new Rectangle(34, parent.getProgressBarPosY(), Environment.FRAME_WIDTH - 40 * 2, 8));
        setVisible(true);
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }
}
