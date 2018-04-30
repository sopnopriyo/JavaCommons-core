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
	public void DeepCopy_string_test() {
		assertEquals("hello", NestedObject.deepCopy("hello"));
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Split Object Path testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void splitObjectPath_test() {
		assertEquals(
			new String[] { "enter", "into", "the", "breach", "0" }, 
			NestedObject.splitObjectPath("enter[into].the.breach[0]")
		);
		assertEquals(
			new String[] { "target", "1", "fire", "0" }, 
			NestedObject.splitObjectPath("target[1].fire[0]")
		);
		assertEquals(
			new String[] { "titan", "hull", "level" }, 
			NestedObject.splitObjectPath("titan['hull']['level']")
		);
	}
}
