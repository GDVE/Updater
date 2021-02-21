package ru.simplemc.updater.gui;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.data.Config;
import ru.simplemc.updater.gui.border.DropShadowBorder;
import ru.simplemc.updater.util.ResourceUtil;
import ru.simplemc.updater.util.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import static ru.simplemc.updater.Settings.BACKGROUND_IMAGE;

public class Frame extends JFrame {

    private Point startClick;
    private int createdButtonsCount;
    private final JLabel exitButton;
    private final JLabel minimizeButton;

    public Frame(String title) {

        try {

            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = defaultToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(defaultToolkit, Settings.FRAME_TITLE);

        } catch (Throwable ignored) {
        }

        setupBackgroundImage();
        setTitle(title);
        setName(title);
        setUndecorated(true);

        if (SystemUtil.isUnix())
            setBackground(new Color(0, 0, 0, 255));
        else
            setBackground(new Color(0, 0, 0, 0));

        setOpacity(1);
        setResizable(false);

        if (SystemUtil.isUnix()) {
            setPreferredSize(new Dimension(Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE * 2, Settings.FRAME_HEIGHT - Settings.FRAME_SHADOW_SIZE * 2));
        } else
            setPreferredSize(new Dimension(Settings.FRAME_WIDTH, Settings.FRAME_HEIGHT));

        setSize(this.getPreferredSize());
        setLayout(new BorderLayout());

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(ResourceUtil.getBufferedImage("icon.png"));

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

    public void setPane(Container container) {

        JPanel pane = (JPanel) container;
        pane.add(exitButton);
        pane.add(minimizeButton);

        if (!SystemUtil.isUnix())
            pane.setBorder(new DropShadowBorder(new Color(0, 0, 0, 0), 5, Settings.FRAME_SHADOW_SIZE, 0.15F, 5, true, true, true, true));

        pane.setOpaque(false);
        pane.setLayout(new BorderLayout());

        setContentPane(pane);
        pack();

        if (!isVisible())
            setVisible(true);
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

        systemButton.setIcon(ResourceUtil.getImageIcon("button/" + actionType + ".png"));
        systemButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionType.equals("exit"))
                    SystemUtil.halt();
                else
                    frame.setState(JFrame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                systemButton.setIcon(ResourceUtil.getImageIcon("button/" + actionType + "_hover.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                systemButton.setIcon(ResourceUtil.getImageIcon("button/" + actionType + ".png"));
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        createdButtonsCount++;

        int axisX = Settings.FRAME_WIDTH - Settings.FRAME_SHADOW_SIZE - 48 * createdButtonsCount;
        int axisY = Settings.FRAME_SHADOW_SIZE;

        if (SystemUtil.isUnix()) {
            axisX -= Settings.FRAME_SHADOW_SIZE;
            axisY -= Settings.FRAME_SHADOW_SIZE;
        }

        systemButton.setBounds(axisX, axisY, 48, 30);

        return systemButton;
    }

    /**
     * Установка фона для программы в зависимости от настроек лаунчера.
     */
    private void setupBackgroundImage() {

        Config launcherConfig = new Config("launcher", false);

        if (launcherConfig.hasValue("launcherSelectedTheme")) {

            String themeName = launcherConfig.getString("launcherSelectedTheme").replace("Тема оформления: ", "");

            switch (themeName) {
                case "Осень":
                    BACKGROUND_IMAGE = "fall";
                    break;
                case "Каньон":
                    BACKGROUND_IMAGE = "canyon";
                    break;
                case "Ночное небо":
                    BACKGROUND_IMAGE = "night";
                    break;
                case "Рассвет":
                    BACKGROUND_IMAGE = "dawn";
                    break;
                case "Озеро":
                    BACKGROUND_IMAGE = "lake";
                    break;
                case "Крепость":
                    BACKGROUND_IMAGE = "fortress";
                    break;
                case "Закат":
                    BACKGROUND_IMAGE = "sunset";
                    break;
                case "Лес":
                    BACKGROUND_IMAGE = "forest";
                    break;
                case "Зима":
                    BACKGROUND_IMAGE = "winter";
                    break;
                default:
                    BACKGROUND_IMAGE = "spring";
                    break;
            }
        }

        // Проверка времени суток (необходимо для некоторых тем оформления)
        if (BACKGROUND_IMAGE.equals("winter") || BACKGROUND_IMAGE.equals("spring")) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
            String currentHour = simpleDateFormat.format(System.currentTimeMillis());
            String[] nightHours = {"17", "18", "19", "20", "21", "22", "23", "00", "01", "02", "03", "04", "05", "06"};

            for (String nightHour : nightHours) {
                if (currentHour.equals(nightHour)) {
                    BACKGROUND_IMAGE = BACKGROUND_IMAGE.replace(BACKGROUND_IMAGE, BACKGROUND_IMAGE + "_night");
                    break;
                }
            }
        }
    }
}
