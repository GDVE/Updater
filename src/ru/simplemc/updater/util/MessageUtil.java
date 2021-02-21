package ru.simplemc.updater.util;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageUtil {

    public static void printError(String title, String message) {
        printError(title, message, true);
    }

    public static void printError(String title, String message, boolean needShutdown) {

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        if (needShutdown)
            SystemUtil.halt();
    }

    public static void printException(String title, String message, Exception exception) {
        printException(title, message, exception, true);
    }

    public static void printException(String title, String message, Exception exception, boolean needShutdown) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        printError(title, message + "\n\n" + stringWriter.toString(), needShutdown);
    }

    public static void printWarning(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void printSuccess(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
