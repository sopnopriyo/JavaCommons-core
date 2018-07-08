package picoded.core.conv;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;
import static picoded.core.conv.NestedObjectFetch.*;

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
	
	//--------------------------------------------------------------------------------------------------
	//
	// Split Object Path testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void splitObjectPath_test() {
		assertEquals(new String[] { "enter", "into", "the", "breach", "0" },
			NestedObjectFetch.splitObjectPath("enter[into].the.breach[0]"));
		assertEquals(new String[] { "target", "1", "fire", "0" },
			NestedObjectFetch.splitObjectPath("target[1].fire[0]"));
		assertEquals(new String[] { "titan", "hull", "level" },
			NestedObjectFetch.splitObjectPath("titan['hull']['level']"));
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Fetch Object testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void fetchObjectSimpleTest() {
		Object base = ConvertJSON.toMap("{ \"a\" : { \"b.c\" : [1,2], \"0\":\"haha\" } }");
		assertEquals(1, NestedObjectFetch.fetchObject(base, "a.b.c[0]"));
		assertEquals(2, NestedObjectFetch.fetchObject(base, "a[b.c][1]"));
		assertEquals("haha", NestedObjectFetch.fetchObject(base, "a.0"));
		assertEquals("haha", NestedObjectFetch.fetchObject(base, "a[0]"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchObjectTest() {
		assertNull(NestedObjectFetch.fetchObject(null, null));
		
		assertNull(NestedObjectFetch.fetchObject(null, null, null));
		assertEquals("default", NestedObjectFetch.fetchObject(null, null, "default"));
		assertEquals("default", NestedObjectFetch.fetchObject("string", null, "default"));
		Map map = new HashMap();
		assertEquals("default", NestedObjectFetch.fetchObject(map, null, "default"));
		assertEquals("default", NestedObjectFetch.fetchObject(map, "", "default"));
		map.put("key", "value");
		assertEquals("value", NestedObjectFetch.fetchObject(map, "key", "default"));
		assertEquals("value", NestedObjectFetch.fetchObject(map, " key ", "default"));
		assertEquals("value", NestedObjectFetch.fetchObject(map, ".key", "default"));
		map.put("key1", "value1");
		assertEquals("value1", NestedObjectFetch.fetchObject(map, "[key1]", "default"));
		assertEquals("default", NestedObjectFetch.fetchObject(map, "[key2]", "default"));
		assertEquals("default", NestedObjectFetch.fetchObject(map, "key.[key2]", "default"));
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchObjectRecursiveTest() {
		Map map = new HashMap();
		map.put("key", "value");
		Map subMap = new HashMap();
		subMap.put("sub_key", "sub_value");
		map.put("key1", subMap);
		assertEquals("sub_value", NestedObjectFetch.fetchObject(map, "key1.sub_key", "default"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchObjectRecursiveArrayTest() {
		Map map = new HashMap();
		map.put("key", "value");
		Map subMap = new HashMap();
		subMap.put("sub_key", "sub_value");
		map.put("key1", subMap);
		assertEquals("sub_value", NestedObjectFetch.fetchObject(map, "[key1][sub_key]", "default"));
		assertEquals("default", NestedObjectFetch.fetchObject(map, "key[key1]", "default"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = RuntimeException.class)
	public void fetchObjectInvalidTest() {
		Map map = new HashMap();
		map.put("key", "value");
		assertEquals("value1", NestedObjectFetch.fetchObject(map, "[key1", "default"));
	}
	
	@Test
	public void splitObjectPathTest() {
		assertArrayEquals(new String[] {}, splitObjectPath(null));
		
		assertEquals(new ArrayList<String>(), splitObjectPath(null, null));
		List<String> ret = new ArrayList<>();
		assertEquals(new ArrayList<String>(), splitObjectPath(null, ret));
		assertEquals(new ArrayList<String>(), splitObjectPath("", ret));
		List<String> key = new ArrayList<String>();
		key.add("my_key");
		assertEquals(key, splitObjectPath(" my_key ", ret));
		ret = new ArrayList<>();
		assertEquals(key, splitObjectPath(".my_key", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("key");
		key.add("my_key");
		assertEquals(key, splitObjectPath("key.my_key", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("my_key");
		assertEquals(key, splitObjectPath("[my_key]", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("\\my_key");
		List<String> expected = new ArrayList<>();
		expected.add("my_key");
		assertEquals(key, splitObjectPath("[\\my_key]", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("\\my_key\\");
		expected = new ArrayList<>();
		expected.add("my_key");
		assertEquals(expected, splitObjectPath("[\"" + "my_key" + "\"]", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("\\my_key\\");
		expected = new ArrayList<>();
		expected.add("key");
		expected.add("my_key");
		assertEquals(expected, splitObjectPath("key[my_key]", ret));
		ret = new ArrayList<>();
		expected = new ArrayList<>();
		expected.add("key");
		assertEquals(expected, splitObjectPath("[ key ]", ret));
		ret = new ArrayList<>();
		key = new ArrayList<>();
		key.add("\\my_key\\");
		expected = new ArrayList<>();
		expected.add("my_key");
		assertEquals(expected, splitObjectPath("[\'" + "my_key" + "\']", ret));
	}
	
	@Test(expected = RuntimeException.class)
	public void splitObjectPathExceptionTest() {
		List<String> ret = new ArrayList<>();
		List<String> key = new ArrayList<String>();
		assertEquals(key, splitObjectPath("[my_key", ret));
	}
	
	//@Test (expected = RuntimeException.class)
	public void splitObjectPathExceptionUnexpectedKeyTest() {
		List<String> key = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		list.add(null);
		list.add("key1");
		assertEquals(key, splitObjectPath("abc[.KEY [[key1.KEY[]", list));
	}
	
}
