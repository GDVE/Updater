package ru.simplemc.updater.gui.pane;

import lombok.Getter;
import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.utils.OSUtils;

import java.awt.*;

public class PaneDownloader extends PaneTextStatus {

    @Getter
    private final ProgressBar progressBar = new ProgressBar(this);

    public PaneDownloader() {
        super("Подготовка", "Пожалуйста подождите...");
        this.add(progressBar);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        this.posY = progressBar.isVisible() ? this.posY - 6 : this.calcDefaultPosY();
        super.paintComponent(graphics);
    }

    public int getProgressBarPosY() {
        return this.posY + 36;
    }
}
