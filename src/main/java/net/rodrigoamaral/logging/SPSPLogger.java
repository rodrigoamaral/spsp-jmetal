package net.rodrigoamaral.logging;


import net.rodrigoamaral.dspsp.project.events.DynamicEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.*;

public class SPSPLogger {

    public static final Logger logger = Logger.getLogger(SPSPLogger.class.getName());

    private static final String RULER = horizontalRuler(80, "-");
    public static final String ENV_LOG_LEVEL = "SPSP_LOG_LEVEL";
    public static final String LOG_DIR = "logs";
    public static final String LOG_FILE = "dspsp.log";

    static {
        try {
            config();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void config() throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.reset();

        final SPSPLogFormatter formatter = new SPSPLogFormatter();

        final Level level = getLevelFromEnv();

        logger.setLevel(level);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(level);
        ch.setFormatter(formatter);
        logger.addHandler(ch);

        File directory = new File(LOG_DIR);
        directory.mkdir();

        FileHandler fh = new FileHandler(LOG_DIR + "/" + LOG_FILE);
        fh.setLevel(level);
        fh.setFormatter(formatter);
        logger.addHandler(fh);

    }

    private static Level getLevelFromEnv() {
        String logLevel = System.getenv(ENV_LOG_LEVEL);

        Level level = Level.INFO;

        if (logLevel != null) {
            logLevel = logLevel.trim().toLowerCase();
            switch (logLevel) {
                case "info":
                    level = Level.INFO;
                    break;
                case "debug":
                    level = Level.FINE;
                    break;
                case "trace":
                    level = Level.FINER;
                    break;
                default:
                    logLevel = "info";
                    System.out.println("SPSPLogger: Invalid value \"" + logLevel + "\" for environment variable " + ENV_LOG_LEVEL + "." +
                            "Must be \"info\", \"debug\" or \"trace\". " +
                            "Program will assume \""+logLevel+"\" by default.");
            }
        } else {
            logLevel = "info";
            System.out.println("SPSPLogger: Environment variable " + ENV_LOG_LEVEL + " not set. " +
                    "Must be \"info\", \"debug\" or \"trace\". " +
                    "Program will assume \""+logLevel+"\" by default.");
        }
        System.out.println("logLevel = " + logLevel);
        return level;
    }


    public static void trace(String msg) {
        logger.finer("(trc): " +msg);
    }

    public static void debug(String msg) {
        logger.fine("(dbg): " + msg);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void rescheduling(int schedulings, DynamicEvent event, int runNumber, int totalRuns) {
        
        String msg = String.format("RESCHEDULING %d: %s at %.4f (run %d of %d) ", schedulings, event.description(), event.getTime(), runNumber, totalRuns);
        
        logger.info(highlight(msg));
    }

    public static void rescheduling(int schedulings, DynamicEvent event) {

        String msg = String.format("RESCHEDULING %d: %s at %.4f", schedulings, event.description(), event.getTime());

        logger.info(highlight(msg));
    }

    public static void printRun(int i, int n) {
        String msg = String.format("RUN %d of %d", i, n);
        logger.info(highlight(msg));
    }

    private static String highlight(String msg) {

        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(RULER);
        sb.append("\n");
        sb.append(msg);
        sb.append("\n");
        sb.append(RULER);

        return sb.toString();
    }

    private static String horizontalRuler(int lenght, String tile) {
        return String.join("", Collections.nCopies(lenght, tile));
    }

}
