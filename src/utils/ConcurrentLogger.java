package utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;

public class ConcurrentLogger extends Thread {

    private static ConcurrentLogger LoggerHolder = null;
    private File logFile;
    private FileHandler logFileHandler;
    private Logger logger;
    ConcurrentLinkedQueue<String> logMessageQueue = new ConcurrentLinkedQueue<String>();

    private ConcurrentLogger() {

        String format = "%5$s ";
        System.setProperty("java.util.logging.SimpleFormatter.format", format);

        String fileName = "log" + ".txt";
        try {
            this.logFile = new File(fileName);
            if (logFile.createNewFile()) {
                // System.out.println("File created: " + logFile.getName());
            } else {
                // System.out.println("File already exists.");
            }
            logFileHandler = new FileHandler(fileName, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleFormatter formatter = new SimpleFormatter();
        logFileHandler.setFormatter(formatter);

        logger = Logger.getLogger("ConcurrentLogger");
        logger.addHandler(logFileHandler);

        if (Constants.CONSOLE_LOGGING) {
            logger.addHandler(new ConsoleHandler());
        } else {
            logger.setUseParentHandlers(false);
        }

        if (Constants.DEBUG) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.INFO);
        }
    }

    public static ConcurrentLogger getInstance() {
        if (LoggerHolder == null) {
            LoggerHolder = new ConcurrentLogger();
        }
        return LoggerHolder;
    }

    public void logInfo(String message) {
        logMessageQueue.add(message);
    }

    @Override
    public void run() {

        while (true) {
            if (!logMessageQueue.isEmpty()) {
                String message = logMessageQueue.poll();
                logger.log(Level.INFO, message);
            }
        }
    }
}
