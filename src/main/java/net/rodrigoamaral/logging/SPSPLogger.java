package net.rodrigoamaral.logging;


import net.rodrigoamaral.dspsp.project.events.DynamicEvent;

import java.util.Collections;
import java.util.logging.*;

public class SPSPLogger {

    public static final Logger logger = Logger.getLogger(SPSPLogger.class.getName());

    private static final String RULER = horizontalRuler(80, "-");

    static {
        config();
    }

    private static void config() {
        LogManager manager = LogManager.getLogManager();

        manager.reset();
        logger.setLevel(Level.INFO);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        ch.setFormatter(new SPSPLogFormatter());
        logger.addHandler(ch);

        // TODO: Implement a log file handler
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

    public static void error(String msg) {
        logger.severe(msg);
    }

    public static void rescheduling(int schedulings, DynamicEvent event) {
        
        String msg = String.format("RESCHEDULING %d: %s at %.4f", schedulings, event.description(), event.getTime());
        
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

    public static void setDebugLevel(boolean debug) {
        if (debug) {
            logger.setLevel(Level.FINE);
        } else {
            logger.setLevel(Level.INFO);
        }
    }
}
