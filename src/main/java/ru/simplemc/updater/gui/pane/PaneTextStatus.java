package ru.simplemc.updater.gui.pane;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.Updater;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import java.awt.*;

public class PaneTextStatus extends PaneBase {

    protected String title;
    protected String message;
    protected int posY;

    public PaneTextStatus(String title, String message) {
        this.setCurrentStatus(title, message);
        this.posY = this.calcDefaultPosY();
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

    protected int calcDefaultPosY() {
        return (int) (Updater.getFrame().getPreferredSize().getHeight() / 2) - 24;
    }
}
