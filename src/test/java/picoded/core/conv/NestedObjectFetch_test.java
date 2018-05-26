package picoded.core.conv;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NestedObjectFetch_test {
	
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
		new NestedObjectFetch();
	}
	
	// //--------------------------------------------------------------------------------------------------
	// //
	// // Deep cloning testing
	// //
	// //--------------------------------------------------------------------------------------------------
	
	// @Test
	// public void DeepCopy_string_test() {
	// 	assertEquals("hello", NestedObject.deepCopy("hello"));
	// }

	//--------------------------------------------------------------------------------------------------
	//
	// Split Object Path testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void splitObjectPath_test() {
		assertEquals(
			new String[] { "enter", "into", "the", "breach", "0" }, 
			NestedObjectFetch.splitObjectPath("enter[into].the.breach[0]")
		);
		assertEquals(
			new String[] { "target", "1", "fire", "0" }, 
			NestedObjectFetch.splitObjectPath("target[1].fire[0]")
		);
		assertEquals(
			new String[] { "titan", "hull", "level" }, 
			NestedObjectFetch.splitObjectPath("titan['hull']['level']")
		);
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Split Object Path testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void fetchObject_test() {
		Object base = ConvertJSON.toMap("{ \"a\" : { \"b.c\" : [1,2], \"0\":\"haha\" } }");
		assertEquals(1, NestedObjectFetch.fetchObject(base, "a.b.c[0]"));
		assertEquals(2, NestedObjectFetch.fetchObject(base, "a[b.c][1]"));
		assertEquals("haha", NestedObjectFetch.fetchObject(base, "a.0"));
		assertEquals("haha", NestedObjectFetch.fetchObject(base, "a[0]"));
	}
}
