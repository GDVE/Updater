package ru.simplemc.updater.gui;

import ru.simplemc.updater.Environment;
import ru.simplemc.updater.config.LauncherConfig;
import ru.simplemc.updater.gui.border.DropShadowBorder;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;
import ru.simplemc.updater.utils.ResourcesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static ru.simplemc.updater.Environment.BACKGROUND_IMAGE;

public class Frame extends JFrame {

    private final JLabel exitButton;
    private final JLabel minimizeButton;
    private Point startClick;
    private int createdButtonsCount;
    private boolean isFirstSetPane = true;

    public Frame() {

        try {
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = defaultToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(defaultToolkit, Environment.FRAME_TITLE);
        } catch (Throwable ignored) {
        }

        setupBackgroundImage();
        setTitle(Environment.FRAME_TITLE);
        setName(Environment.FRAME_TITLE);
        setUndecorated(true);

        if (OSUtils.isLinux())
            setBackground(new Color(0, 0, 0, 255));
        else
            setBackground(new Color(0, 0, 0, 0));

        setOpacity(1);
        setResizable(false);

        if (OSUtils.isLinux() || OSUtils.isMacOS()) {
            setPreferredSize(new Dimension(Environment.FRAME_WIDTH - Environment.FRAME_SHADOW_SIZE * 2,
                    Environment.FRAME_HEIGHT - Environment.FRAME_SHADOW_SIZE * 2));
        } else
            setPreferredSize(new Dimension(Environment.FRAME_WIDTH, Environment.FRAME_HEIGHT));

        setSize(this.getPreferredSize());
        setLayout(new BorderLayout());

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(ResourcesUtils.getOrCreateBufferedImage("icon.png"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startClick = e.getPoint();
                getComponentAt(startClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int posX = getLocation().x;
                int posY = getLocation().y;
                int offsetX = (posX + e.getX()) - (posX + startClick.x);
                int offsetY = (posY + e.getY()) - (posY + startClick.y);
                int x = posX + offsetX;
                int y = posY + offsetY;
                setLocation(x, y);
            }
        });

        exitButton = createSystemButton("exit", this);
        minimizeButton = createSystemButton("minimize", this);
    }

    /**
     * Устанавливает и отрисовывает главную панель программы
     *
     * @param container - контейнер для установки в качестве главной панели
     */
    public void setPane(Container container) {

        JPanel pane = (JPanel) container;
        pane.add(exitButton);
        pane.add(minimizeButton);

        if (OSUtils.isWindows() && isFirstSetPane) {
            pane.setBorder(new DropShadowBorder(new Color(0x0, true), 5,
                    Environment.FRAME_SHADOW_SIZE,
                    0.1F,
                    5,
                    true, true, true, true));
            isFirstSetPane = false;
        }

        pane.setOpaque(false);
        pane.setLayout(new BorderLayout());
        setContentPane(pane);
        pack();

        if (!isVisible()) setVisible(true);
    }

    /**
     * Создает кнопку отвечающую за определенные системные функции (свернуть, развернуть)
     *
     * @param actionType - тип действия (exit, minimize)
     * @param frame      - JFrame с которым будут работать кнопки
     * @return - возвращает готовый JLabel с текстурой кнопки
     */
    private JLabel createSystemButton(final String actionType, final Frame frame) {

        final JLabel systemButton = new JLabel();

        systemButton.setIcon(ResourcesUtils.getOrCreateImageIcon("button/" + actionType + ".png"));
        systemButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionType.equals("exit"))
                    ProgramUtils.haltProgram();
                else
                    frame.setState(JFrame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                systemButton.setIcon(ResourcesUtils.getOrCreateImageIcon("button/" + actionType + "_hover.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                systemButton.setIcon(ResourcesUtils.getOrCreateImageIcon("button/" + actionType + ".png"));
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        createdButtonsCount++;

        int axisX = Environment.FRAME_WIDTH - Environment.FRAME_SHADOW_SIZE - 48 * createdButtonsCount;
        int axisY = Environment.FRAME_SHADOW_SIZE;

        if (OSUtils.isLinux() || OSUtils.isMacOS()) {
            axisX -= Environment.FRAME_SHADOW_SIZE;
            axisY -= Environment.FRAME_SHADOW_SIZE;
        }

        systemButton.setBounds(axisX, axisY, 48, 30);
        return systemButton;
    }

    /**
     * Установка фона для программы в зависимости от настроек лаунчера.
     */
    private void setupBackgroundImage() {

        LauncherConfig launcherConfig = new LauncherConfig();
        try {
            launcherConfig.readFromDisk();
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (launcherConfig.getSelectedTheme() != null) {
            Map<String, String> backgroundByName = new HashMap<>();
            backgroundByName.put("Тема оформления: Лес", "forest");
            backgroundByName.put("Тема оформления: Берег", "lake");
            backgroundByName.put("Тема оформления: Секретная база", "sweet_home");
            backgroundByName.put("Тема оформления: Фабрика", "factory");
            BACKGROUND_IMAGE = backgroundByName.getOrDefault(launcherConfig.getSelectedTheme(), "forest");
        }
    }
}
