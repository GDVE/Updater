package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import java.awt.*;

public class PaneTextStatus extends PaneBase {

    protected String title;
    protected String message;
    protected int posY;

    public PaneTextStatus(String title, String message) {
        this(title, message, OSUtils.isWindows() ? 62 : 70);
    }

    public PaneTextStatus(String title, String message, int posY) {
        this.setCurrentStatus(title, message);
        this.posY = posY;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawString(graphics, posY, true, title);
        drawString(graphics, posY + 22, false, message);
    }

    public void setCurrentStatus(String title, String message) {
        this.title = title.toUpperCase();
        this.message = message;
        repaint();
    }

    protected void drawString(Graphics graphics, int posY, boolean titled, String string) {
        graphics.setColor(titled ? Environment.TITLE_FONT_COLOR : Environment.MESSAGE_FONT_COLOR);
        graphics.setFont(ResourcesUtils.getOrCreateFont(
                titled ? "FSElliotPro-Heavy" : "FSElliotPro-Bold",
                titled ? 22 : 16)
        );

        graphics.drawString(string, 34, posY);
    }
}
