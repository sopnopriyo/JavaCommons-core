package picoded.core.conv;

/**
 * Test cases for Date Converter. Apart from testing the happy cases ,
 * This class would also test the exceptional cases as well
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class DateConv_test {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    /**
     * Invalid constructor test
     * Expected exception
     *
     * @throws Exception
     */
    @Test(expected = IllegalAccessError.class)
    public void invalidConstructor() throws Exception {
        new DateConv();

    }

    /**
     * Test for happy cases to convert String to java.util.Date
     */
    @Test
    public void fromDateStringHappyCase() {
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateInString = "7-Jun-2013";
        Date exceptedDate = null;
        Date actualDate = null;
        try {
            actualDate = DateConv.fromDateString(dateInString, dateFormat);
            exceptedDate = formatter.parse(dateInString);
        } catch (ParseException parseException) {
            assertEquals(null, parseException);
        }
        assertEquals(exceptedDate, actualDate);
    }

    /**
     * Test for exceptional cases to convert Date from String to java.util.Date
     */
    @Test(expected = IllegalArgumentException.class)
    public void fromDateStringExceptionCase() {
        String dateFormat = "dd-MMM-yyfyy";
        Date actualDate = DateConv.fromDateString("32-Jun-2013", dateFormat);
    }

    /**
     * Test for happy cases to convert java.util.Date to String
     */
    @Test
    public void toDateStringHappyCase() {
        String pattern = "dd-MMM-yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = new Date();
        String actualDateString = DateConv.toDateString(date, pattern);
        assertEquals(actualDateString, formatter.format(date));
        assertNotEquals(actualDateString, "dd-MMM-yysyy");
    }

    /**
     * Test for converting unix timestamp to Date String
     */
    @Test
    public void fromUnixTimestampTest() {
        String pattern = "dd-MMM-yyyy";
        long unixTimestamp = 1372339860;
        String actualDate = DateConv.toDateString(unixTimestamp, pattern);
        String expectedDate = "27-Jun-2013";
        assertEquals(expectedDate, actualDate);
    }

    /**
     * Test for formatting a date from one pattern to another
     */
    @Test
    public void reformatCode() {
        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "dd-MM-yyyy";
        String actualDate = DateConv.reformatDate("27-Jun-2013", inputPattern, outputPattern);
        String expectedDate = "27-06-2013";
        assertEquals(expectedDate, actualDate);
    }
}