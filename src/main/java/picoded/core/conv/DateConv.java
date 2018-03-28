package picoded.core.conv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import picoded.core.exception.ExceptionMessage;

/**
 * Convenience class to convert between Date types.
 * [NOTE: This is not vetted for general usage]
 * ---------------------------------------------------------------------------------
 * Common Date Pattern                    |   Example Date
 * ---------------------------------------------------------------------------------
 * dd-MM-yy                                 31-01-12
 * dd-MM-yyyy	                            31-01-2012
 * MM-dd-yyyy	                            01-31-2012
 * yyyy-MM-dd	                            2012-01-31
 * yyyy-MM-dd HH:mm:ss	                    2012-01-31 23:59:59
 * yyyy-MM-dd HH:mm:ss.SSS a	            2012-01-31 10:59:59.999 PM
 * yyyy-MM-dd HH:mm:ss.SSSZ	                2012-01-31 23:59:59.999+0100
 * EEEEE MMMMM yyyy HH:mm:ss.SSSZ	        Saturday November 2012 10:45:42.720+0100
 * ----------------------------------------------------------------------------------
 **/
public class DateConv extends DateUtils {

    /**
     * Invalid constructor (throws exception)
     **/
    protected DateConv() {
        throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
    }

    /**
     * Convert a Date from String to java.util.Date Class
     * Given a Date as a String and its pattern
     * Then, this function will build the Java Date object out of that  DateString and pattern
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date fromDateString(final String dateStr, final String pattern) {
        try {
            return DateUtils.parseDate(dateStr, new String[]{pattern});
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a java.util.Date Object to String
     * Specify the Date format and provide java.util.Date Object in the parameter
     * This function would constructs Date String according to the format given
     * ```
     * // Conversion to a date string, from milliseconds timestamp
     * DateConv.toDateString( new Date(timestamp), "YYYY-MM-DD" );
     * ```
     *
     * @param date
     * @param outputPattern
     * @return
     */
    public static String toDateString(final Date date, final String outputPattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(outputPattern);
        return formatter.format(date);
    }

    /**
     * Convert Unix timestamp to Date in String according to the output pattern
     *
     * @param timestamp
     * @param outputPattern
     * @return
     */
    public static String toDateString(final long timestamp, final String outputPattern) {
        return toDateString(new Date((long) timestamp * 1000), outputPattern);
    }

    /**
     * Format a date String from one pattern to another
     *
     * @param dateStr
     * @param inputPattern
     * @param outputPattern
     * @return
     */
    public static String reformatDate(String dateStr, String inputPattern, String outputPattern) {
        return toDateString(fromDateString(dateStr, inputPattern), outputPattern);
    }
}
