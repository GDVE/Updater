package ru.simplemc.updater.gui.utils;

import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageUtils {

    public static void printErrorWithShutdown(String title, String message) {
        printErrorWithShutdown(title, message, true);
    }

    public static void printErrorWithShutdown(String title, String message, boolean needShutdown) {

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        if (needShutdown)
            ProgramUtils.haltProgram();
    }

    public static void printException(String title, String message, Exception exception) {
        printException(title, message, exception, true);
    }

    public static void printException(String title, String message, Exception exception, boolean needShutdown) {
        printErrorWithShutdown(title, message + "\n\n" + getStringFromThrowable(exception), needShutdown);
    }

    public static void printWarning(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void printSuccess(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void printFullStackTraceWithExit(String message, Throwable throwable) {
        printFullStackTraceWithExit(message, throwable, true);
    }

    public static void printFullStackTraceWithExit(String message, Throwable throwable, boolean needExit) {

        Thread thread = Thread.currentThread();

        new Thread(() -> {

            JFrame frame = new JFrame("Произошла ошибка в работе лаунчера!");

            try {
                frame.setIconImage(ImageIO.read(MessageUtils.class.getResource("/assets/images/icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            frame.setSize(new Dimension(602, 470));
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    if (needExit)
                        ProgramUtils.haltProgram();
                    else
                        thread.resume();
                }
            });

            JPanel panel = new JPanel(new FlowLayout());

            TextArea textArea = new TextArea(20, 81);
            textArea.setText(getStringFromThrowable(throwable));
            textArea.setEditable(false);
            textArea.setBackground(Color.decode("#ffe0e0"));

            Button copyButton = new Button("Скопировать ошибку");
            copyButton.addActionListener(e -> OSUtils.copyToClipboard(textArea.getText()));

            Button reportButton = new Button("Сообщить администрации");
            reportButton.addActionListener(e -> OSUtils.openLinkInSystemBrowser("https://vk.com/gim56175786"));

            panel.add(new Label(message));
            panel.add(textArea);
            panel.add(new Label("Пожалуйста сообщите нам о ошибке, для её исправления и улучшения работы нашего лаунчера!"));
            panel.add(copyButton);
            panel.add(reportButton);

            frame.setContentPane(panel);
            frame.setVisible(true);

        }).start();
        thread.suspend();
    }

    public static String getStringFromThrowable(Throwable throwable) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }

}
