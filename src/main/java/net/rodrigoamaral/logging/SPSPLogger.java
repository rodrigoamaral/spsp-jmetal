package net.rodrigoamaral.logging;


import java.util.logging.*;

public class SPSPLogger {

    public static final Logger logger = Logger.getLogger(SPSPLogger.class.getName());

    static {
        config();
    }

    private static void config() {
        LogManager manager = LogManager.getLogManager();

        manager.reset();
        logger.setLevel(Level.ALL);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        ch.setFormatter(new SPSPLogFormatter());
        logger.addHandler(ch);

        // TODO: Implement a log file handler
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void severe(String msg) {
        logger.severe(msg);
    }
}
