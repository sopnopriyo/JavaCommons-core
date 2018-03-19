package picoded.core.conv;

// Target test class

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
// Test Case include
import org.junit.Before;
import org.junit.Test;

import picoded.core.conv.DateTimeConv.ISODateFormat;

import static org.junit.Assert.*;

public class DateTimeConv_test {
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
		
	}
	
	//
	// Expected exception testing
	//
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new DateTimeConv();
		
	}
	
	@Test
	public void convMilliSecondsToISO() {
		long millisecondsDate = Long.parseLong("1441756800000");
		
		//check case
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millisecondsDate);
		String calISODate = "0" + cal.get(Calendar.DATE) + "-0" + (cal.get(Calendar.MONTH) + 1) + "-"
			+ cal.get(Calendar.YEAR);
		
		String isoDate = DateTimeConv.toISOFormat(millisecondsDate, ISODateFormat.DDMMYYYY, "-");
		
		assertEquals(calISODate, isoDate);
	}
	
	@Test
	public void convISOToMilliseconds() {
		String isoDate = "1990-05-20";
		
		String millisecondsDate = DateTimeConv.toMillisecondsFormat(isoDate, ISODateFormat.YYYYMMDD, "-");
		
		String isoDateReconstructed = DateTimeConv.toISOFormat(Long.parseLong(millisecondsDate),
			ISODateFormat.YYYYMMDD, "-");
		
		assertEquals(isoDate, isoDateReconstructed);
		isoDate = "2016-10-25";
		millisecondsDate = DateTimeConv.toMillisecondsFormat(isoDate, ISODateFormat.YYYYMMDD, "-");
		isoDateReconstructed = DateTimeConv.toISOFormat(Long.parseLong(millisecondsDate),
			ISODateFormat.YYYYMMDD, "-");
		assertEquals(isoDate, isoDateReconstructed);
	}

	@Test
	public void convISOtoSeconds(){
		String isoDate = "1990-05-20";

		String millisecondsDate = DateTimeConv.toMillisecondsFormat(isoDate, ISODateFormat.YYYYMMDD, "-");
        long longDate = Long.parseLong(millisecondsDate);

        String isoDateReconstructedToSeconds = DateTimeConv.toSecondsFormat(isoDate, ISODateFormat.YYYYMMDD, "-");

        assertEquals(String.valueOf(longDate/1000L), isoDateReconstructedToSeconds);
	}

	@Test
	public void changeISOFormat() {
		
		long millisecondsDate = Long.parseLong("1431756800000"); //16-5-2015
		String isoDate_dmy = DateTimeConv.toISOFormat(millisecondsDate, ISODateFormat.DDMMYYYY, "-");
		
		assertNull(DateTimeConv.changeISODateFormat(null, null, null, null));
		assertNull(DateTimeConv.changeISODateFormat("", ISODateFormat.DDMMYYYY, ISODateFormat.YYYYMMDD,
			null));
		
		isoDate_dmy = DateTimeConv.toISOFormat(millisecondsDate, ISODateFormat.DDMMYYYY, "-");
		assertEquals("16-05-2015", isoDate_dmy);
		
		isoDate_dmy = DateTimeConv.toISOFormat(millisecondsDate, ISODateFormat.DDMMYYYY, null);
		assertEquals("16-05-2015", isoDate_dmy);
		
		String isoDate_ymd = DateTimeConv.changeISODateFormat(isoDate_dmy, ISODateFormat.DDMMYYYY,
			ISODateFormat.YYYYMMDD, "-");
		assertEquals("2015-05-16", isoDate_ymd);
		
		String isoDate_mdy = DateTimeConv.changeISODateFormat(isoDate_ymd, ISODateFormat.YYYYMMDD,
			ISODateFormat.MMDDYYYY, "-");
		assertEquals("05-16-2015", isoDate_mdy);
		
		String isoDate_ydm = DateTimeConv.changeISODateFormat(isoDate_mdy, ISODateFormat.MMDDYYYY,
			ISODateFormat.YYYYDDMM, "-");
		assertEquals("2015-16-05", isoDate_ydm);
		
	}
	
	@Test
	public void toISODateFormat() {
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat(null));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat(""));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat("ddmmyyyy"));
		assertEquals(ISODateFormat.MMDDYYYY, DateTimeConv.toISODateFormat("mmddyyyy"));
		assertEquals(ISODateFormat.YYYYMMDD, DateTimeConv.toISODateFormat("yyyymmdd"));
		assertEquals(ISODateFormat.YYYYDDMM, DateTimeConv.toISODateFormat("yyyyddmm"));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat("abc"));
	}
	
	@Test
	public void toMillisecondsFormat() {
		assertNull(DateTimeConv.toMillisecondsFormat(null, null, null));
		assertNull(DateTimeConv.toMillisecondsFormat("", null, null));
		assertNull(DateTimeConv.toMillisecondsFormat("2016-10-25-12", null, "-"));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat(""));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat("ddmmyyyy"));
		assertEquals(ISODateFormat.MMDDYYYY, DateTimeConv.toISODateFormat("mmddyyyy"));
		assertEquals(ISODateFormat.YYYYMMDD, DateTimeConv.toISODateFormat("yyyymmdd"));
		assertEquals(ISODateFormat.YYYYDDMM, DateTimeConv.toISODateFormat("yyyyddmm"));
		assertEquals(ISODateFormat.DDMMYYYY, DateTimeConv.toISODateFormat("abc"));
	}
	
	@Test
	public void isInISOFormat() {
		assertTrue(DateTimeConv.isInISOFormat("-str-"));
		assertFalse(DateTimeConv.isInISOFormat("str"));
	}
	
	@Test
	public void isInMillisecondsFormat() {
		assertTrue(DateTimeConv.isInMillisecondsFormat("-str"));
		assertFalse(DateTimeConv.isInMillisecondsFormat("str-"));
		assertTrue(DateTimeConv.isInMillisecondsFormat("str"));
	}
	
	@Test
	public void getCurrentDateISO() {
		assertNotNull(DateTimeConv.getCurrentDateISO(ISODateFormat.DDMMYYYY, null));
		assertNotNull(DateTimeConv.getCurrentDateISO(ISODateFormat.DDMMYYYY, "-"));
	}

	@Test
	public void fromDateStringHappyCase () {
		String dateFormat = "dd-MMM-yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String dateInString = "7-Jun-2013";
		Date exceptedDate = null;
		Date actualDate = null;
		try {
			actualDate = DateTimeConv.fromDateString(dateInString, dateFormat);
			exceptedDate = formatter.parse(dateInString);
		}
		catch (ParseException parseException) {
			assertEquals(null, parseException);
		}
		assertEquals(exceptedDate, actualDate);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromDateStringExceptionCase() {
		String dateFormat = "dd-MMM-yyfyy";
		try {
			Date actualDate = DateTimeConv.fromDateString("32-Jun-2013", dateFormat);
		}
		catch (ParseException parseException) { }
	}

	@Test
	public void toDateStringHappyCase() {
		String pattern = "dd-MMM-yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date date = new Date();
		String actualDateString = DateTimeConv.toDateString(date, pattern);
		assertEquals(actualDateString, formatter.format(date));
		assertNotEquals(actualDateString, "dd-MMM-yysyy");
	}
}