package utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class ConcurrentLogger {
	private static final SimpleDateFormat fecha = new SimpleDateFormat("[dd:mm:yyyy]-[HH:mm:ss]: ");
	public static PrintStream archivo;
	
	public static void createLog() throws FileNotFoundException {
		Date resultdate = new Date(System.currentTimeMillis());
		System.out.println("Log: Creando archivo log...");
		archivo = new PrintStream(new File("log"+fecha.format(resultdate)+".txt"));
		System.setOut(archivo);
	}
	
	public static String timeStamp() {
		//Funcion global para imprimir timestamps en consola, y por lo tanto en el log
		SimpleDateFormat date_format = new SimpleDateFormat("[HH:mm:ss:SSS]: ");
		Date resultdate = new Date(System.currentTimeMillis());
		return date_format.format(resultdate);
	}
	
	public static void writeLog(String str) {
		System.out.println(timeStamp()+str);
	}
}
































/* package utils;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.time.LocalDateTime;

public class ConcurrentLogger extends Thread {

    private static ConcurrentLogger LoggerHolder = null;
    private File logFile;
    private FileHandler logFileHandler;
    private Logger logger;
    ConcurrentLinkedQueue<String> logMessageQueue = new ConcurrentLinkedQueue<String>();

    private ConcurrentLogger() {

        String fileName = "log_" + LocalDateTime.now() + ".txt";
        try {
            File logFile = new File(fileName);
            if(logFile.createNewFile()) {
               System.out.println("File created: " + logFile.getName());
            } else {
               System.out.println("File already exists.");
            }
            logFileHandler = new FileHandler(fileName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleFormatter formatter = new SimpleFormatter();
        logFileHandler.setFormatter(formatter);

        logger = Logger.getLogger("ConcurrentLogger");
        logger.addHandler(logFileHandler);

        if (Constants.CONSOLE_LOGGING) {
            logger.addHandler(new ConsoleHandler());
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

    // Una funcion por cada tipo de mensaje para poder usar diferentes colas de ser necesario.
    public void logInfo(String message) {
        logMessageQueue.add("INFO - " + message);
    }

    public void logDebug(String message) {
        logMessageQueue.add("DEBUG - " + message);
    }

    public void logError(String message) {
        logMessageQueue.add("ERROR - " + message);
    }

    @Override
    public void run() {

        while (true) {
            if (!logMessageQueue.isEmpty()) {
                String message = logMessageQueue.poll();
                if (message.startsWith("INFO")) {
                    logger.log(Level.INFO, message);
                } else if (message.startsWith("DEBUG")) {
                    logger.log(Level.FINEST, message);
                } else if (message.startsWith("ERROR")) {
                    logger.log(Level.SEVERE, message);
                }
            }
        }
    }
} */

