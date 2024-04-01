package server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * ServerLogger provides a simple logging utility for the server application. It supports
 * logging messages at different levels (INFO, ERROR) to stdout with a timestamp prefix.
 * The class is designed to be easily extendable for additional log levels or output destinations.
 */
public class ServerLogger {

    // Date format for the timestamp in log messages that maintains millisecond precision.
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Generates the current timestamp in a specified format.
     * This method centralizes the timestamp formatting logic for reuse across different log methods.
     *
     * @return A string representing the current timestamp in 'yyyy-MM-dd HH:mm:ss.SSS' format.
     */
    private static String getCurrentFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        return dateFormat.format(new Date());
    }

    /**
     * Logs an informational message to stdout. Each log entry includes a timestamp,
     * the log level (INFO), and the provided message.
     *
     * @param msg The message to be logged.
     */
    public static void info(String msg) {
        System.out.println(String.format("[PST-Time-Zone] %s [Level] INFO, [Message] %s", getCurrentFormattedTime(), msg));
    }

    /**
     * Logs an error message to stdout. Each log entry includes a timestamp,
     * the log level (ERROR), and the provided message.
     *
     * @param msg The message to be logged.
     */
    public static void error(String msg) {
        System.out.println(String.format("[PST-Time-Zone] %s [Level] ERROR, [Message] %s", getCurrentFormattedTime(), msg));
    }
}
