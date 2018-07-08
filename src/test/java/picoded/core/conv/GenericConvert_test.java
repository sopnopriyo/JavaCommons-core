package picoded.core.conv;

//Target test class
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static picoded.core.conv.GenericConvert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import picoded.core.struct.GenericConvertList;
import picoded.core.struct.GenericConvertMap;
import picoded.core.struct.ProxyGenericConvertList;
import picoded.core.struct.ProxyGenericConvertMap;

/**
 * Test suite to verify the methods inside GenericConvert.java
 */
public class GenericConvert_test {
	
	private static final double DELTA = 1e-15;
	
	/**
	 *  Setup the temp vars
	 */
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
		
	}
	
	/**
	 * Expected exception testing
	 */
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new GenericConvert();
	}
	
	/**
	 * Test cases to assert toString functionality
	 */
	@Test
	public void toStringTest() {
		// Case 1: Test conversion returns non-null value back
		assertNotNull(GenericConvert.toString(-1, null));
		
		// Case 2: Test for null values and null fallback
		assertNull("", GenericConvert.toString(null, null));
		
		// Case 3: Test fallback kicks in
		assertEquals("fallback", GenericConvert.toString(null, "fallback"));
		
		// Case 4: Test String conversion convert properly
		assertEquals("inout", GenericConvert.toString("inout", null));
		
		// Case 5: Test numeric conversion to string
		assertEquals("1", GenericConvert.toString(new Integer(1), null));
		assertEquals("1", GenericConvert.toString(new Integer(1)));
	}
	
	/**
	 * Test cases to assert toBoolean functionality
	 */
	@Test
	public void toBooleanTest() {
		// Case 1: Conversion of objects will return false
		List<String> list = new ArrayList<String>();
		assertFalse(GenericConvert.toBoolean(list, false));
		
		// Case 2: For boolean true false test case
		Boolean boolean1 = new Boolean(true);
		assertTrue(GenericConvert.toBoolean(boolean1, false));
		
		boolean1 = new Boolean(false);
		assertFalse(GenericConvert.toBoolean(boolean1, true));
		
		// Case 3: For boolean number test case
		assertTrue(GenericConvert.toBoolean(1, true));
		
		assertFalse(toBoolean(new Boolean("1"), false));
		
		assertFalse(GenericConvert.toBoolean(0, true));
		
		// Case 4: For null test case, test fallback
		assertFalse(GenericConvert.toBoolean(null, false));
		assertTrue(toBoolean(null, true));
		
		// Case 5: For String value test cases
		assertFalse(GenericConvert.toBoolean("", false));
		assertTrue(GenericConvert.toBoolean("+", false));
		assertTrue(GenericConvert.toBoolean("t", false));
		assertTrue(GenericConvert.toBoolean("T", false));
		assertTrue(GenericConvert.toBoolean("y", false));
		assertTrue(GenericConvert.toBoolean("Y", false));
		
		assertFalse(GenericConvert.toBoolean("-", false));
		assertFalse(GenericConvert.toBoolean("f", false));
		assertFalse(GenericConvert.toBoolean("F", false));
		assertFalse(GenericConvert.toBoolean("n", false));
		assertFalse(GenericConvert.toBoolean("N", false));
		
		assertTrue(GenericConvert.toBoolean("123", false));
		assertTrue(GenericConvert.toBoolean("12", false));
		assertFalse(GenericConvert.toBoolean("-1", false));
		assertFalse(GenericConvert.toBoolean("$%", false));
		
		assertTrue(toBoolean(999, false));
		assertFalse(toBoolean("000", false));
	}
	
	/**
	 * toBoolean: Single parameter without fallback test
	 */
	@Test
	public void toBooleanSingleParameterTest() {
		assertTrue(toBoolean("true"));
	}
	
	/**
	 * Number Conversion test cases
	 */
	@Test
	public void toNumberTest() {
		// Case 1: Converting non-numeric values test
		List<String> list = new ArrayList<String>();
		assertNotEquals(list, GenericConvert.toNumber(list, 0).intValue());
		assertNotEquals("$%", GenericConvert.toNumber("$%", 0).intValue());
		
		// Case 2: Convert numeric to numeric
		assertEquals(10, GenericConvert.toNumber(10, 0).intValue());
		
		// Case 3: Fallback test
		assertNotEquals("", GenericConvert.toNumber("", 0).intValue());
		
		// Case 4: Convert string to numeric
		assertEquals(new BigDecimal("01111111111111111"),
			GenericConvert.toNumber("01111111111111111", 0));
		
		assertEquals(new BigDecimal("2.1"), GenericConvert.toNumber("2.1", null));
		assertEquals(new BigDecimal("2.2"), GenericConvert.toNumber("2.2"));
	}
	
	/**
	 * toNumber: Single parameter without fallback test
	 */
	@Test
	public void toNumberSingleTest() {
		assertEquals(new BigDecimal("1"), GenericConvert.toNumber("1"));
	}
	
	/**
	 * Integer conversion test
	 */
	@Test
	public void toIntTest() {
		// Case 1: Fallback test
		assertEquals(1, GenericConvert.toInt(null, 1));
		
		// Case 2: Integer to integer conversion
		assertEquals(2, GenericConvert.toInt(2, 1));
		
		// Case 3: Single parameter test
		assertEquals(3, GenericConvert.toInt(3));
		
		// @TODO: Case 4: String to integer
		
		// @TODO: Case 5: Object to integer
		
	}
	
	/**
	 * Long conversion test
	 */
	@Test
	public void toLongTest() {
		// Case 1: Fallback test
		assertEquals(1l, GenericConvert.toLong(null, 1l));
		
		// Case 2: Long to long conversion
		assertEquals(2l, GenericConvert.toLong(2l, 1l));
		
		// Case 3: Single parameter test
		assertEquals(3l, GenericConvert.toLong(3l));
		
		// @TODO: Case 4: String to long
		
		// @TODO: Case 5: Object to long
	}
	
	/**
	 * Float conversion test
	 */
	@Test
	public void toFloatTest() {
		// Case 1: Fallback test
		assertEquals(1.0f, GenericConvert.toFloat(null, 1.0f), DELTA);
		
		// Case 2: Long to long conversion
		assertEquals(2.0f, GenericConvert.toFloat(2.0f, 1.0f), DELTA);
		
		// Case 3: Single parameter test
		assertEquals(3.0f, GenericConvert.toFloat(3.0f), DELTA);
		
		// @TODO: Case 4: String to long
		
		// @TODO: Case 5: Object to long
	}
	
	@Test
	public void toDoubleTest() {
		assertEquals(1.0, GenericConvert.toDouble(null, 1.0), DELTA);
		assertEquals(2.0, GenericConvert.toDouble("2.0", 1.0), DELTA);
		assertEquals(3.0, GenericConvert.toDouble(3.0), DELTA);
		assertEquals(4.0, GenericConvert.toDouble(4.0, 1.0), DELTA);
	}
	
	@Test
	public void toByteTest() {
		assertEquals((byte) 'a', GenericConvert.toByte(null, (byte) 'a'));
		assertEquals((byte) 'b', GenericConvert.toByte("a", (byte) 'b'));
		assertEquals((byte) 'c', GenericConvert.toByte((byte) 'c'));
		assertEquals((byte) 4.0, GenericConvert.toByte(4.0, (byte) 'd'));
	}
	
	@Test
	public void toShortTest() {
		assertEquals((short) 'a', GenericConvert.toShort(null, (short) 'a'));
		assertEquals((short) 'b', GenericConvert.toShort("a", (short) 'b'));
		assertEquals((short) 'c', GenericConvert.toShort((short) 'c'));
		assertEquals((short) 4.0, GenericConvert.toShort(4.0, (short) 'd'));
	}
	
	@Test
	public void toUUIDTest() {
		assertNull(GenericConvert.toUUID("hello-world"));
		assertEquals(GUID.fromBase58("heijoworjdabcdefghijabc"),
			GenericConvert.toUUID(GUID.fromBase58("heijoworjdabcdefghijabc")));
		assertEquals(GUID.fromBase58("123456789o123456789o12"),
			GenericConvert.toUUID("123456789o123456789o12", null));
		assertNull(GenericConvert.toUUID("123456789o123456789o1o2", null));
		assertEquals(GUID.fromBase58("heijoworjdabcdefghijabc"),
			GenericConvert.toUUID(GUID.fromBase58("heijoworjdabcdefghijabc"), null));
		assertEquals(GUID.fromBase58("heijoworjdabcdefghijabc"),
			GenericConvert.toUUID(null, GUID.fromBase58("heijoworjdabcdefghijabc")));
		assertNull(GenericConvert.toUUID(null, null));
		assertEquals(GUID.fromBase58("heijoworjdabcdefghijabc"),
			GenericConvert.toUUID(GUID.fromBase58("heijoworjdabcdefghijabc"), "hello world"));
		assertNull(GenericConvert.toUUID(1234567, "default"));
		assertNull(GenericConvert.toUUID("123456789o123456789o12345", "default"));
	}
	
	@Test
	public void toGUIDTest() {
		assertNull(GenericConvert.toGUID("hello-world"));
		assertEquals("ADAukG8u3ryYrm6pHFDB6o",
			GenericConvert.toGUID(GUID.fromBase58("heijoworjdabcdefghijabc")));
		assertEquals("ADAukG8u3ryYrm6pHFDB6o", GenericConvert.toGUID("ADAukG8u3ryYrm6pHFDB6o"));
		assertNull(GenericConvert.toGUID(100));
		assertEquals("ADAukG8u3ryYrm6pHFDB6o", GenericConvert.toGUID(null, "ADAukG8u3ryYrm6pHFDB6o"));
		assertEquals("ADAukG8u3ryYrm6pHFDB6o", GenericConvert.toGUID("ADAukG8u3ryYrm6pHFDB6o", null));
		assertNull(GenericConvert.toGUID(null, null));
		assertEquals("ADAukG8u3ryYrm6pHFDB6o",
			GenericConvert.toGUID("ADAukG8u3ryYrm6pHFDB6o", "hello world"));
		List<String> list = new ArrayList<String>();
		assertNotEquals(list, GenericConvert.toNumber(list, 0).intValue());
		assertNotEquals("$%", GenericConvert.toNumber("$%", 0).intValue());
		assertEquals(10, GenericConvert.toNumber(10, 0).intValue());
		assertNotEquals("", GenericConvert.toNumber("", 0).intValue());
		assertEquals(new BigDecimal("01111111111111111"),
			GenericConvert.toNumber("01111111111111111", 0));
		UUID uuid = UUID.randomUUID();
		assertNotNull(toGUID(uuid, null));
		
	}
	
	// @Test
	// public void biFunctionMapTest() {
	// 	assertNotNull(biFunctionMap());
	// 	Map<Class<?>, BiFunction<Object, Object, ?>> myBiFunctionMap = new HashMap<Class<?>, BiFunction<Object, Object, ?>>();
	// 	biFunctionMap = myBiFunctionMap;
	// 	assertEquals(myBiFunctionMap, biFunctionMap);
	// }
	
	@Test
	public void toStringArrayTest() {
		assertNull(toStringArray(null));
		
		assertNull(toStringArray(null, null));
		
		assertNull(toStringArray(null, "default"));
		assertArrayEquals(new String[] { "key1", "key2" },
			toStringArray(new String[] { "key1", "key2" }, "default"));
		assertArrayEquals(new String[] { "1", "2.2" },
			toStringArray(new Object[] { "1", 2.2 }, "default"));
		assertArrayEquals(new String[] { "key1", "key2", "key3" },
			toStringArray("[\"key1\",\"key2\",\"key3\"]", "default"));
		List<String> list = new ArrayList<>();
		list.add("key1");
		list.add("key2");
		assertArrayEquals(new String[] { "key1", "key2" }, toStringArray(list, "default"));
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		//assertArrayEquals(new String[]{"key1", "key2"}, toStringArray(2.2, "default"));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void toStringArrayExceptionTest() {
		List list = new ArrayList<>();
		list.add(2.23);
		list.add("3.4");
		list.add("key1");
		assertArrayEquals(new String[] { "2.23", "3.4", "key1" }, toStringArray(list, "default"));
	}
	
	@Test
	public void toObjectArrayTest() {
		assertNull(toObjectArray(null));
		
		assertNull(toObjectArray(null, null));
		assertNull(toObjectArray(null, "default"));
		assertArrayEquals(new Object[] { "key1", "key2" },
			toObjectArray(new String[] { "key1", "key2" }, "default"));
		assertArrayEquals(new Object[] { "1", "2.2" },
			toObjectArray(new String[] { "1", "2.2" }, "default"));
		assertArrayEquals(new Object[] { "key1", "key2", "key3" },
			toObjectArray("[\"key1\",\"key2\",\"key3\"]", "default"));
		List<String> list = new ArrayList<>();
		list.add("key1");
		list.add("key2");
		assertArrayEquals(new String[] { "key1", "key2" }, toObjectArray(list, "default"));
	}
	
	@Test
	public void toListTest() {
		assertNull(toList(null));
		
		assertNull(toList(null, null));
		
		assertNull(toList(null, "default"));
		assertEquals(Arrays.asList(new Object[] { "key1", "key2" }),
			toList(new String[] { "key1", "key2" }, "default"));
		assertEquals(Arrays.asList(new Object[] { "1", "2.2" }),
			toList(new String[] { "1", "2.2" }, "default"));
		assertEquals(Arrays.asList(new Object[] { "key1", "key2", "key3" }),
			toList("[\"key1\",\"key2\",\"key3\"]", "default"));
		List<String> list = new ArrayList<>();
		list.add("key1");
		list.add("key2");
		assertEquals(Arrays.asList(new String[] { "key1", "key2" }), toList(list, "default"));
		assertEquals(Arrays.asList(new Object[] { "key1", "key2", "key3" }),
			toList(new StringBuilder("[\"key1\",\"key2\",\"key3\"]"), "default"));
	}
	
	@Test
	public void toStringMapTest() {
		assertNull(toStringMap(null));
		
		assertNull(toStringMap(null, null));
		assertNull("default", toStringMap(null, "default"));
		Map<String, String> map = new HashMap<>();
		assertEquals(map, toStringMap(map, null));
		map = new HashMap<String, String>();
		map.put("Hello", "WORLD");
		map.put("WORLD", "Hello");
		String str = ConvertJSON.fromMap(map);
		assertEquals(map, toStringMap(str, null));
	}
	
	// @Test(expected = RuntimeException.class)
	// public void getBiFunction_noisySelfTest() {
	// 	assertNotNull(getBiFunction_noisy(getClass()));
	// }
	// 
	// @Test
	// public void getBiFunction_noisyTest() {
	// 	biFunctionMap = null;
	// 	assertNotNull(getBiFunction_noisy(String.class));
	// 	assertNotNull(getBiFunction_noisy(String[].class));
	// }
	
	@Test
	public void toStringArrayForceToStringTest() {
		assertNull(toArrayHelper(null));
		StringBuilder sb = new StringBuilder("[\"key1\",\"key2\",\"key3\"]");
		assertArrayEquals(new String[] { "key1", "key2", "key3" }, toStringArray(sb, null));
	}
	
	@Test
	public void toGenericConvertListTest() {
		assertNull(toGenericConvertList(null));
		
		assertNull(toGenericConvertList(null, null));
		
		List<String> list = new ArrayList<String>();
		GenericConvertList<String> gcList = new ProxyGenericConvertList<String>(list);
		assertEquals(list, toGenericConvertList(gcList, null));
		assertEquals(list, toGenericConvertList(list, "default"));
		assertNull(toGenericConvertList("true", "default"));
		
		list = new ArrayList<String>();
		list.add("key");
		assertEquals(list, toGenericConvertList(null, list));
		
		list = new ArrayList<String>();
		list.add("key");
		gcList = new ProxyGenericConvertList<String>(list);
		assertEquals(list, toGenericConvertList(gcList, "default"));
		
		assertEquals(list, toGenericConvertList(ConvertJSON.fromList(list), "default"));
		
		list = new ArrayList<String>();
		list.add(null);
		assertEquals(list, toGenericConvertList(ConvertJSON.fromList(list), "default"));
		
		list = null;
		assertEquals(list, toGenericConvertList(ConvertJSON.fromList(list), "default"));
	}
	
	@Test
	public void toGenericConvertStringMapTest() {
		assertNull(toGenericConvertStringMap(null));
		
		assertNull(toGenericConvertStringMap(null, null));
		assertNull(toGenericConvertStringMap(null, "default"));
		
		assertNull(toGenericConvertStringMap("true", "default"));
		
		Map<String, String> defMap = new HashMap<String, String>();
		defMap.put("key", "value");
		assertEquals(defMap, toGenericConvertStringMap(null, defMap));
		
		GenericConvertMap<String, String> gcMap = new ProxyGenericConvertMap<String, String>(defMap);
		assertEquals(defMap, toGenericConvertStringMap(gcMap, defMap));
		
		Map<String, String> empty = new ProxyGenericConvertMap<String, String>();
		assertEquals(defMap, toGenericConvertStringMap(defMap, empty));
		
		assertEquals(defMap, toGenericConvertStringMap(ConvertJSON.fromMap(defMap), empty));
		assertNotNull(toGenericConvertStringMap("{}", empty));
		
		assertNotNull(toGenericConvertStringMap(123456, empty));
		
		defMap = null;
		assertEquals(defMap, toGenericConvertStringMap(ConvertJSON.fromMap(defMap), "default"));
	}
	
}
