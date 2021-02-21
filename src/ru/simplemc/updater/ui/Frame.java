package ru.simplemc.updater.ui;

import ru.simplemc.updater.Settings;
import ru.simplemc.updater.ui.border.DropShadowBorder;
import ru.simplemc.updater.util.ResourceUtil;
import ru.simplemc.updater.util.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class Frame extends JFrame {

    private Point startClick;
    private int createdButtonsCount;
    private final JLabel exitButton;
    private final JLabel minimizeButton;

    public Frame(String title) {

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
}
