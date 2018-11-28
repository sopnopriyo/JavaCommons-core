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

public class ListCollection_test {
	
	public static class TestCollection extends GenericConvertHashMap<String,List<String>> implements ListCollection<String,String> {
		public List<String> fetchSubList(String key) {
			List<String> ret = get(key);
			if(ret == null) {
				ret = new ArrayList<String>();
				put(key, ret);
			}
			return ret;
		}
	}
	

	private TestCollection arrayListMap = null;
	
	@Before
	public void setUp() {
		arrayListMap = new TestCollection();
	}
	
	@After
	public void tearDown() {
		arrayListMap = null;
	}
	
	@Test
	public void addToListTest() {
		arrayListMap.append("key", "val");
		assertEquals(1, arrayListMap.size());
		assertEquals("val", arrayListMap.getListValue("key", 0));
	}
	
}
