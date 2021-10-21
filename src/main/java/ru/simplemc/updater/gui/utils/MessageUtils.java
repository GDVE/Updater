package ru.simplemc.updater.gui.utils;

import ru.simplemc.updater.utils.ProgramUtils;

import javax.swing.*;

public class MessageUtils {

    public static void printErrorWithShutdown(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        ProgramUtils.haltProgram();
    }
}
