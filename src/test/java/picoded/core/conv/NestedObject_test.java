package picoded.core.conv;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NestedObject_test {
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
		
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Expected exception testing
	//
	//--------------------------------------------------------------------------------------------------
	
	// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new NestedObject();
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Deep cloning testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void stringDeepCopy() {
		assertEquals("hello", NestedObject.deepCopy("hello"));
	}
}
