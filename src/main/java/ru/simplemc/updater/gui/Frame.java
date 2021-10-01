package ru.simplemc.updater.gui;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.gui.pane.PaneTextStatus;
import ru.simplemc.updater.utils.ResourcesUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class Frame extends JFrame {

    public Frame() {

        try {
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = defaultToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(defaultToolkit, Environment.FRAME_TITLE);
        } catch (Throwable ignored) {
        }

        this.setTitle(Environment.FRAME_TITLE);
        this.setName(Environment.FRAME_TITLE);
        this.setBackground(Color.decode("#ffffff"));
        this.setResizable(false);
        this.setPreferredSize(new Dimension(Environment.FRAME_WIDTH, Environment.FRAME_HEIGHT));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setIconImage(ResourcesUtils.getOrCreateBufferedImage("icon.png"));
    }

    public void setPane(Container container) {
        container.setLayout(new BorderLayout());
        setContentPane(container);
        pack();
    }

    public void setStatus(String title, String message) {

        Container container = getContentPane();

        if (container instanceof PaneTextStatus) {
            ((PaneTextStatus) container).setCurrentStatus(title, message);
        }

        setPane(new PaneTextStatus(title, message));
    }
}
