package ru.simplemc.updater.gui.pane;

import lombok.Getter;
import ru.simplemc.updater.gui.ProgressBar;
import ru.simplemc.updater.utils.OSUtils;

import java.awt.*;

public class PaneDownloader extends PaneTextStatus {

    @Getter
    private final ProgressBar progressBar = new ProgressBar();

    public PaneDownloader() {
        super("Подготовка", "Пожалуйста подождите...");
        this.add(progressBar);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        this.posY = progressBar.isVisible()
                ? OSUtils.isWindows() ? 52 : 60
                : OSUtils.isWindows() ? 62 : 70;
        super.paintComponent(graphics);
    }
}
