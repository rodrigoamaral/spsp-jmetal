package net.rodrigoamaral.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SPSPLogFormatter extends SimpleFormatter {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder(1000);

        sb.append(df.format(new Date(record.getMillis()))).append(" ");

        if (record.getLevel() == Level.WARNING || record.getLevel() == Level.SEVERE) {
            sb.append("[").append(record.getLevel()).append("] ");
        }

        sb.append(formatMessage(record)).append("\n");

        return sb.toString();
    }
}
