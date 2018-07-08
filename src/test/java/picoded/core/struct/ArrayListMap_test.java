package picoded.core.struct;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArrayListMap_test {
	
	private ArrayListMap<String, String> arrayListMap = null;
	
	@Before
	public void setUp() {
		arrayListMap = new ArrayListMap<String, String>();
	}
	
	@After
	public void tearDown() {
		arrayListMap = null;
	}
	
	///
	/// Testing against one append at a time
	///
	@Test
	public void arrayAppendTest() {
		String[] exp = new String[] { "brand", "new", "world" };
		List<String> list = new ArrayList<String>();
		list.add(exp[0]);
		list.add(exp[1]);
		list.add(exp[2]);
		assertArrayEquals(exp, list.toArray(new String[list.size()]));
	}
	
	///
	/// Testing against .toMapArray conversion
	///
	@Test
	public void toMapArrayConversion() {
		String[] exp = new String[] { "brand", "new", "world" };
		arrayListMap.append("hello", exp[0]);
		arrayListMap.append("hello", exp[1]);
		arrayListMap.append("hello", exp[2]);
		
		Map<String, String[]> cObj = arrayListMap.toMapArray(exp);
		assertArrayEquals(exp, cObj.get("hello"));
	}
	
	///
	/// Testing against multiple append
	///
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void multiArrayAppendTest() {
		
		String[] dataArr = new String[] { "one", "two", "three" };
		List<String> data = Arrays.asList(dataArr);
		List<String> blankList = Arrays.asList(new String[] {});
		
		// Data to test
		arrayListMap.append("hello", data);
		
		// Test that blank data is handled peacefully
		arrayListMap.append("hello", blankList);
		arrayListMap.append("hello", (List) null);
		arrayListMap.append("hello", (Enumeration) null);
		
		Map<String, String[]> cObj = arrayListMap.toMapArray(dataArr);
		assertArrayEquals(dataArr, cObj.get("hello"));
	}
	
	@Test
	public void notnullObjectTest() {
		assertNotNull(arrayListMap);
	}
	
	@Test
	public void addToListTest() {
		arrayListMap.append("key", "val");
		assertEquals(1, arrayListMap.size());
	}
	
	@Test
	public void toStringTest() {
		arrayListMap.append("key", "val");
		assertNotNull(arrayListMap.toString());
	}
	
}
