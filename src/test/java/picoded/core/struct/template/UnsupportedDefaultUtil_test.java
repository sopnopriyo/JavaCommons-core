package picoded.core.struct.template;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnsupportedDefaultUtil_test {
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test(expected = IllegalAccessError.class)
	public void constructorInvalidTest() {
		UnsupportedDefaultUtil UnsupportedDefaultUtil = new UnsupportedDefaultUtil();
		assertNotNull(UnsupportedDefaultUtil);
	}
	
	@Test
	public void checkIndexRangeTest() {
		UnsupportedDefaultUtil.checkIndexRange(0, 1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkIndexRangeInvalid1Test() {
		UnsupportedDefaultUtil.checkIndexRange(-1, 1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkIndexRangeInvalid2Test() {
		UnsupportedDefaultUtil.checkIndexRange(3, 1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkIndexRangeInvalid3Test() {
		UnsupportedDefaultUtil.checkIndexRange(3, 3);
	}
	
	@Test
	public void checkInsertRangeTest() {
		UnsupportedDefaultUtil.checkInsertRange(0, 1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkInsertRangeInvalid1Test() {
		UnsupportedDefaultUtil.checkInsertRange(-1, 1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkInsertRangeInvalid2Test() {
		UnsupportedDefaultUtil.checkInsertRange(3, 1);
	}
}
