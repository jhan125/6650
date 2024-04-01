package client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides logging functionalities for the client application. It supports logging
 * messages at different levels (INFO, ERROR) to the standard output, prefixed with
 * a timestamp for better traceability.
 */
public class ClientLogger {

    // Date format pattern used for logging timestamps.
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Generates a formatted timestamp string based on the current system time.
     * This method centralizes timestamp formatting to ensure consistency across all logs.
     *
     * @return A string representing the current timestamp in the specified format.
     */
    private static String getCurrentFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        return dateFormat.format(new Date());
    }

    /**
     * Logs an informational message to the standard output with a timestamp and INFO level.
     *
     * @param msg The message to be logged.
     */
    public static void info(String msg) {
        System.out.println(String.format("[PST-Time-Zone] %s [Level] INFO, [Message] %s", getCurrentFormattedTime(), msg));
    }

    /**
     * Logs an error message to the standard output with a timestamp and ERROR level.
     *
     * @param msg The message to be logged.
     */
    public static void error(String msg) {
        System.err.println(String.format("[PST-Time-Zone] %s [Level] ERROR, [Message] %s", getCurrentFormattedTime(), msg));
    }
}
