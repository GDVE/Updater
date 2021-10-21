package ru.simplemc.updater.service.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

public class SimpleLogger {

    private static final SimpleLoggerWriter WRITER = new SimpleLoggerWriter("updater");

    private final String prefix;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public SimpleLogger(Class<?> clazz) {
        this(clazz.getSimpleName());
    }

    public SimpleLogger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String message) {
        print(message, "INFO");
    }

    public void error(String message) {
        print(message, "ERROR");
    }

    public void error(String message, Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        error(message + "\n" + writer);
    }

    private void print(String message, String channel) {
        WRITER.writeToLog("[" + dateFormat.format(System.currentTimeMillis()) +
                " - " + prefix + "] [" + channel + "] " + message);
    }
}
