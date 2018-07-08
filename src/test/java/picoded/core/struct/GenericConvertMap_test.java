package picoded.core.struct;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import picoded.core.struct.template.StandardHashMap_test;

public class GenericConvertMap_test extends StandardHashMap_test {
	
	class GenericConvertTest<K, V> implements GenericConvertMap<K, V> {
		
	}
	
	class ProxyTest<K, V> implements GenericConvertMap<K, V> {
		HashMap<K, V> base = new HashMap<>();
		
		@Override
		public V put(K key, V value) {
			return base.put(key, value);
		}
		
		@Override
		public V get(Object key) {
			return base.get(key);
		}
		
		@Override
		public Set<K> keySet() {
			return base.keySet();
		}
		
		@Override
		public V remove(Object key) {
			return base.remove(key);
		}
	}
	
	GenericConvertMap<String, Object> unsupported = null;
	ProxyTest<String, Object> proxyMap = null;
	
	@Override
	@Before
	public void setUp() {
		unsupported = new GenericConvertTest<>();
		map = new ProxyTest<String, Object>();
		proxyMap = new ProxyTest<String, Object>();
	}
	
	@Override
	@After
	public void tearDown() {
		unsupported = null;
		map = null;
		proxyMap = null;
	}
	
	@Test
	public void buildTest() {
		assertNotNull(GenericConvertMap.build(new HashMap<String, String>()));
	}
	
	//
	// GET based testing
	//
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringTest() {
		assertEquals("", unsupported.getString("my_key"));
	}
	
	@Test
	public void getStringValidTest() {
		proxyMap.put("my_key", "key");
		assertEquals("key", proxyMap.getString("my_key"));
	}
	
	@Test
	public void getStringInvalidTest() {
		proxyMap.put("my_key", "key");
		assertEquals(null, proxyMap.getString("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringOverloadTest() {
		assertEquals("", unsupported.getString("my_key", "my_object"));
	}
	
	@Test
	public void getStringOverloadValidTest() {
		proxyMap.put("my_key", "key");
		assertEquals("key", proxyMap.getString("my_key", "my_object"));
	}
	
	@Test
	public void getStringOverloadValidAlternateTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals("my_object", proxyMap.getString("my_key", "my_object"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getBooleanTest() {
		assertEquals("", unsupported.getBoolean("my_key"));
	}
	
	@Test
	public void getBooleanValidTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.getBoolean("my_key"));
	}
	
	@Test
	public void getBooleanValidFalseTest() {
		proxyMap.put("my_key", "my_object");
		assertFalse(proxyMap.getBoolean("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getBooleanOverloadTest() {
		assertEquals("", unsupported.getBoolean("my_key", true));
	}
	
	@Test
	public void getBooleanOverloadValidTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.getBoolean("my_key", true));
	}
	
	@Test
	public void getBooleanOverloadValidTrueTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.getBoolean("my_key", true));
	}
	
	@Test
	public void getBooleanOverloadValidFalseTest() {
		proxyMap.put("my_key", "my_object");
		assertFalse(proxyMap.getBoolean("my_key", false));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getNumberTest() {
		assertEquals("", unsupported.getNumber("my_key"));
	}
	
	@Test
	public void getNumberValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(BigDecimal.valueOf(1), proxyMap.getNumber("my_key"));
	}
	
	@Test
	public void getNumberNullTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getNumber("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getNumberOverloadTest() {
		assertEquals("", unsupported.getNumber("my_key", 1));
	}
	
	@Test
	public void getNumberOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(BigDecimal.valueOf(1), proxyMap.getNumber("my_key", 5));
	}
	
	@Test
	public void getNumberOverloadValidAlternateTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5, proxyMap.getNumber("my_key1", 5));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getIntTest() {
		assertEquals("", unsupported.getInt("my_key"));
	}
	
	@Test
	public void getIntValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.getInt("my_key"));
	}
	
	@Test
	public void getIntInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(0, proxyMap.getInt("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getIntOverloadTest() {
		assertEquals("", unsupported.getInt("my_key", 1));
	}
	
	@Test
	public void getIntOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.getInt("my_key", 5));
	}
	
	@Test
	public void getIntOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(5, proxyMap.getInt("my_key1", 5));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getLongTest() {
		assertEquals("", unsupported.getLong("my_key"));
	}
	
	@Test
	public void getLongValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1l, proxyMap.getLong("my_key"));
	}
	
	@Test
	public void getLongInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(0l, proxyMap.getLong("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getLongOverloadTest() {
		assertEquals("", unsupported.getLong("my_key", 1l));
	}
	
	@Test
	public void getLongOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1l, proxyMap.getLong("my_key", 5l));
	}
	
	@Test
	public void getLongOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(5l, proxyMap.getLong("my_key1", 5l));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getFloatTest() {
		assertEquals("", unsupported.getFloat("my_key"));
	}
	
	@Test
	public void getFloatValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.getFloat("my_key"), 0.01);
	}
	
	@Test
	public void getFloatInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0f, proxyMap.getFloat("my_key"), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getFloatOverloadTest() {
		assertEquals("", unsupported.getFloat("my_key", 1f));
	}
	
	@Test
	public void getFloatOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.getFloat("my_key", 1f), 0.01);
	}
	
	@Test
	public void getFloatOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5f, proxyMap.getFloat("my_key1", 5f), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getDoubleTest() {
		assertEquals("", unsupported.getDouble("my_key"));
	}
	
	@Test
	public void getDoubleValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.getDouble("my_key"), 0.01);
	}
	
	@Test
	public void getDoubleInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0f, proxyMap.getDouble("my_key1"), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getDoubleOverloadTest() {
		assertEquals("", unsupported.getDouble("my_key", 1d));
	}
	
	@Test
	public void getDoubleOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1d, proxyMap.getDouble("my_key", 5d), 0.01);
	}
	
	@Test
	public void getDoubleOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5d, proxyMap.getDouble("my_key1", 5d), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getByteTest() {
		assertEquals("", unsupported.getByte("my_key"));
	}
	
	@Test
	public void getByteValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 1, proxyMap.getByte("my_key"));
	}
	
	@Test
	public void getByteInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals((byte) 0, proxyMap.getByte("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getByteOverloadTest() {
		assertEquals("", unsupported.getByte("my_key", (byte) 'a'));
	}
	
	@Test
	public void getByteOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 1, proxyMap.getByte("my_key", (byte) 'a'));
	}
	
	@Test
	public void getByteOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 'a', proxyMap.getByte("my_key1", (byte) 'a'));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getShortTest() {
		assertEquals("", unsupported.getShort("my_key"));
	}
	
	@Test
	public void getShortValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.getShort("my_key"));
	}
	
	@Test
	public void getShortInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0, proxyMap.getShort("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getShortOverloadTest() {
		assertEquals("", unsupported.getShort("my_key", (short) 'a'));
	}
	
	@Test
	public void getShortOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.getShort("my_key", (short) 'a'));
	}
	
	@Test
	public void getShortOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals((short) 'a', proxyMap.getShort("my_key1", (short) 'a'));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getUUIDTest() {
		assertEquals("", unsupported.getUUID("my_key"));
	}
	
	@Test
	public void getUUIDValidTest() {
		proxyMap.put("my_key", "my_object");
		assertNull(proxyMap.getUUID("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getUUIDOverloadTest() {
		assertEquals("", unsupported.getUUID("my_key", "ok"));
	}
	
	@Test
	public void getUUIDOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getUUID("my_key", "ok"));
	}
	
	@Test
	public void getUUIDOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertNotNull(proxyMap.getUUID("my_key", "o123456789o123456789ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGUIDTest() {
		assertEquals("", unsupported.getGUID("my_key"));
	}
	
	@Test
	public void getGUIDValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGUID("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGUIDOverloadTest() {
		assertEquals("", unsupported.getGUID("my_key", "ok"));
	}
	
	@Test
	public void getGUIDOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGUID("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getObjectListTest() {
		assertNull(unsupported.getObjectList("my_key"));
	}
	
	@Test
	public void getObjectListValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getObjectList("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getObjectListOverloadTest() {
		assertEquals("", unsupported.getObjectList("my_key", "ok"));
	}
	
	@Test
	public void getObjectListOverloadValidTest() {
		List<String> list = new ArrayList<String>();
		list.add("me");
		proxyMap.put("my_key", "1");
		assertEquals(list, proxyMap.getObjectList("my_key", list));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringMapTest() {
		assertEquals("", unsupported.getStringMap("my_key"));
	}
	
	@Test
	public void getStringMapValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getStringMap("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringMapOverloadTest() {
		assertEquals("", unsupported.getStringMap("my_key", "ok"));
	}
	
	@Test
	public void getStringMapOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getStringMap("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGenericConvertStringMapTest() {
		assertEquals("", unsupported.getGenericConvertStringMap("my_key"));
	}
	
	@Test
	public void getGenericConvertStringMapValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGenericConvertStringMap("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGenericConvertStringMapOverloadTest() {
		assertEquals("", unsupported.getGenericConvertStringMap("my_key", "ok"));
	}
	
	@Test
	public void getGenericConvertStringMapOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGenericConvertStringMap("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGenericConvertListTest() {
		assertEquals("", unsupported.getGenericConvertList("my_key"));
	}
	
	@Test
	public void getGenericConvertListValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGenericConvertList("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getGenericConvertListOverloadTest() {
		assertEquals("", unsupported.getGenericConvertList("my_key", "ok"));
	}
	
	@Test
	public void getGenericConvertListOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getGenericConvertList("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringArrayTest() {
		assertEquals("", unsupported.getStringArray("my_key"));
	}
	
	@Test
	public void getStringArrayValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.getStringArray("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getStringArrayOverloadTest() {
		assertEquals("", unsupported.getStringArray("my_key", "ok"));
	}
	
	@Test
	public void getStringArrayOverloadValidTest() {
		String[] strArr = new String[] { "1", "2" };
		proxyMap.put("my_key", strArr);
		assertArrayEquals(strArr, proxyMap.getStringArray("my_key", strArr));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getObjectArrayTest() {
		assertNull(unsupported.getObjectArray("my_key"));
	}
	
	@Test
	public void getObjectArrayValidTest() {
		proxyMap.put("my_key", "1, 2");
		assertNull(proxyMap.getObjectArray("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void getObjectArrayOverloadTest() {
		assertNull(unsupported.getObjectArray("my_key", "ok"));
	}
	
	@Test
	public void getObjectArrayOverloadValidTest() {
		Object[] strArr = new Object[] { "1", 2 };
		proxyMap.put("my_key", strArr);
		assertArrayEquals(strArr, proxyMap.getObjectArray("my_key", strArr));
	}
	
	//
	// FETCH based testing
	//
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringTest() {
		assertEquals("", unsupported.fetchString("my_key"));
	}
	
	@Test
	public void fetchStringValidTest() {
		proxyMap.put("my_key", "key");
		assertEquals("key", proxyMap.fetchString("my_key"));
	}
	
	@Test
	public void fetchStringInvalidTest() {
		proxyMap.put("my_key", "key");
		assertEquals(null, proxyMap.fetchString("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringOverloadTest() {
		assertEquals("", unsupported.fetchString("my_key", "my_object"));
	}
	
	@Test
	public void fetchStringOverloadValidTest() {
		proxyMap.put("my_key", "key");
		assertEquals("key", proxyMap.fetchString("my_key", "my_object"));
	}
	
	@Test
	public void fetchStringOverloadValidAlternateTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals("my_object", proxyMap.fetchString("my_key", "my_object"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchBooleanTest() {
		assertEquals("", unsupported.fetchBoolean("my_key"));
	}
	
	@Test
	public void fetchBooleanValidTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.fetchBoolean("my_key"));
	}
	
	@Test
	public void fetchBooleanValidFalseTest() {
		proxyMap.put("my_key", "my_object");
		assertFalse(proxyMap.fetchBoolean("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchBooleanOverloadTest() {
		assertEquals("", unsupported.fetchBoolean("my_key", true));
	}
	
	@Test
	public void fetchBooleanOverloadValidTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.fetchBoolean("my_key", true));
	}
	
	@Test
	public void fetchBooleanOverloadValidTrueTest() {
		proxyMap.put("my_key", "true");
		assertTrue(proxyMap.fetchBoolean("my_key", true));
	}
	
	@Test
	public void fetchBooleanOverloadValidFalseTest() {
		proxyMap.put("my_key", "my_object");
		assertFalse(proxyMap.fetchBoolean("my_key", false));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchNumberTest() {
		assertEquals("", unsupported.fetchNumber("my_key"));
	}
	
	@Test
	public void fetchNumberValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(BigDecimal.valueOf(1), proxyMap.fetchNumber("my_key"));
	}
	
	@Test
	public void fetchNumberNullTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchNumber("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchNumberOverloadTest() {
		assertEquals("", unsupported.fetchNumber("my_key", 1));
	}
	
	@Test
	public void fetchNumberOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(BigDecimal.valueOf(1), proxyMap.fetchNumber("my_key", 5));
	}
	
	@Test
	public void fetchNumberOverloadValidAlternateTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5, proxyMap.fetchNumber("my_key1", 5));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchIntTest() {
		assertEquals("", unsupported.fetchInt("my_key"));
	}
	
	@Test
	public void fetchIntValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.fetchInt("my_key"));
	}
	
	@Test
	public void fetchIntInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(0, proxyMap.fetchInt("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchIntOverloadTest() {
		assertEquals("", unsupported.fetchInt("my_key", 1));
	}
	
	@Test
	public void fetchIntOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.fetchInt("my_key", 5));
	}
	
	@Test
	public void fetchIntOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(5, proxyMap.fetchInt("my_key1", 5));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchLongTest() {
		assertEquals("", unsupported.fetchLong("my_key"));
	}
	
	@Test
	public void fetchLongValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1l, proxyMap.fetchLong("my_key"));
	}
	
	@Test
	public void fetchLongInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(0l, proxyMap.fetchLong("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchLongOverloadTest() {
		assertEquals("", unsupported.fetchLong("my_key", 1l));
	}
	
	@Test
	public void fetchLongOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1l, proxyMap.fetchLong("my_key", 5l));
	}
	
	@Test
	public void fetchLongOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(5l, proxyMap.fetchLong("my_key1", 5l));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchFloatTest() {
		assertEquals("", unsupported.fetchFloat("my_key"));
	}
	
	@Test
	public void fetchFloatValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.fetchFloat("my_key"), 0.01);
	}
	
	@Test
	public void fetchFloatInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0f, proxyMap.fetchFloat("my_key"), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchFloatOverloadTest() {
		assertEquals("", unsupported.fetchFloat("my_key", 1f));
	}
	
	@Test
	public void fetchFloatOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.fetchFloat("my_key", 1f), 0.01);
	}
	
	@Test
	public void fetchFloatOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5f, proxyMap.fetchFloat("my_key1", 5f), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchDoubleTest() {
		assertEquals("", unsupported.fetchDouble("my_key"));
	}
	
	@Test
	public void fetchDoubleValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1f, proxyMap.fetchDouble("my_key"), 0.01);
	}
	
	@Test
	public void fetchDoubleInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0f, proxyMap.fetchDouble("my_key1"), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchDoubleOverloadTest() {
		assertEquals("", unsupported.fetchDouble("my_key", 1d));
	}
	
	@Test
	public void fetchDoubleOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1d, proxyMap.fetchDouble("my_key", 5d), 0.01);
	}
	
	@Test
	public void fetchDoubleOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(5d, proxyMap.fetchDouble("my_key1", 5d), 0.01);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchByteTest() {
		assertEquals("", unsupported.fetchByte("my_key"));
	}
	
	@Test
	public void fetchByteValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 1, proxyMap.fetchByte("my_key"));
	}
	
	@Test
	public void fetchByteInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals((byte) 0, proxyMap.fetchByte("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchByteOverloadTest() {
		assertEquals("", unsupported.fetchByte("my_key", (byte) 'a'));
	}
	
	@Test
	public void fetchByteOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 1, proxyMap.fetchByte("my_key", (byte) 'a'));
	}
	
	@Test
	public void fetchByteOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals((byte) 'a', proxyMap.fetchByte("my_key1", (byte) 'a'));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchShortTest() {
		assertEquals("", unsupported.fetchShort("my_key"));
	}
	
	@Test
	public void fetchShortValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.fetchShort("my_key"));
	}
	
	@Test
	public void fetchShortInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals(0, proxyMap.fetchShort("my_key1"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchShortOverloadTest() {
		assertEquals("", unsupported.fetchShort("my_key", (short) 'a'));
	}
	
	@Test
	public void fetchShortOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals(1, proxyMap.fetchShort("my_key", (short) 'a'));
	}
	
	@Test
	public void fetchShortOverloadInvalidTest() {
		proxyMap.put("my_key", "my_object");
		assertEquals((short) 'a', proxyMap.fetchShort("my_key1", (short) 'a'));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchUUIDTest() {
		assertEquals("", unsupported.fetchUUID("my_key"));
	}
	
	@Test
	public void fetchUUIDValidTest() {
		proxyMap.put("my_key", "my_object");
		assertNull(proxyMap.fetchUUID("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchUUIDOverloadTest() {
		assertEquals("", unsupported.fetchUUID("my_key", "ok"));
	}
	
	@Test
	public void fetchUUIDOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchUUID("my_key", "ok"));
	}
	
	@Test
	public void fetchUUIDOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertNotNull(proxyMap.fetchUUID("my_key", "o123456789o123456789ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGUIDTest() {
		assertEquals("", unsupported.fetchGUID("my_key"));
	}
	
	@Test
	public void fetchGUIDValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGUID("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGUIDOverloadTest() {
		assertEquals("", unsupported.fetchGUID("my_key", "ok"));
	}
	
	@Test
	public void fetchGUIDOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGUID("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectListTest() {
		assertNull(unsupported.fetchObjectList("my_key"));
	}
	
	@Test
	public void fetchObjectListValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchObjectList("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectListOverloadTest() {
		assertEquals("", unsupported.fetchObjectList("my_key", "ok"));
	}
	
	@Test
	public void fetchObjectListOverloadValidTest() {
		List<String> list = new ArrayList<String>();
		list.add("me");
		proxyMap.put("my_key", "1");
		assertEquals(list, proxyMap.fetchObjectList("my_key", list));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringMapTest() {
		assertEquals("", unsupported.fetchStringMap("my_key"));
	}
	
	@Test
	public void fetchStringMapValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchStringMap("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringMapOverloadTest() {
		assertEquals("", unsupported.fetchStringMap("my_key", "ok"));
	}
	
	@Test
	public void fetchStringMapOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchStringMap("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGenericConvertStringMapTest() {
		assertEquals("", unsupported.fetchGenericConvertStringMap("my_key"));
	}
	
	@Test
	public void fetchGenericConvertStringMapValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGenericConvertStringMap("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGenericConvertStringMapOverloadTest() {
		assertEquals("", unsupported.fetchGenericConvertStringMap("my_key", "ok"));
	}
	
	@Test
	public void fetchGenericConvertStringMapOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGenericConvertStringMap("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGenericConvertListTest() {
		assertEquals("", unsupported.fetchGenericConvertList("my_key"));
	}
	
	@Test
	public void fetchGenericConvertListValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGenericConvertList("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchGenericConvertListOverloadTest() {
		assertEquals("", unsupported.fetchGenericConvertList("my_key", "ok"));
	}
	
	@Test
	public void fetchGenericConvertListOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchGenericConvertList("my_key", "ok"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringArrayTest() {
		assertEquals("", unsupported.fetchStringArray("my_key"));
	}
	
	@Test
	public void fetchStringArrayValidTest() {
		proxyMap.put("my_key", "1");
		assertNull(proxyMap.fetchStringArray("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchStringArrayOverloadTest() {
		assertEquals("", unsupported.fetchStringArray("my_key", "ok"));
	}
	
	@Test
	public void fetchStringArrayOverloadValidTest() {
		String[] strArr = new String[] { "1", "2" };
		proxyMap.put("my_key", strArr);
		assertArrayEquals(strArr, proxyMap.fetchStringArray("my_key", strArr));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectArrayTest() {
		assertNull(unsupported.fetchObjectArray("my_key"));
	}
	
	@Test
	public void fetchObjectArrayValidTest() {
		proxyMap.put("my_key", "1, 2");
		assertNull(proxyMap.fetchObjectArray("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectArrayOverloadTest() {
		assertNull(unsupported.fetchObjectArray("my_key", "ok"));
	}
	
	@Test
	public void fetchObjectArrayOverloadValidTest() {
		Object[] strArr = new Object[] { "1", 2 };
		proxyMap.put("my_key", strArr);
		assertArrayEquals(strArr, proxyMap.fetchObjectArray("my_key", strArr));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectTest() {
		assertEquals("", unsupported.fetchObject("my_key"));
	}
	
	@Test
	public void fetchObjectValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals("1", proxyMap.fetchObject("my_key"));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void fetchObjectOverloadTest() {
		assertEquals("", unsupported.fetchObject("my_key", "ok"));
	}
	
	@Test
	public void fetchObjectOverloadValidTest() {
		proxyMap.put("my_key", "1");
		assertEquals("1", proxyMap.fetchObject("my_key", "ok"));
	}
	
	@Test
	public void fetchObjectOverloadInvalidTest() {
		proxyMap.put("my_key", "1");
		assertEquals("ok", proxyMap.fetchObject("my_key1", "ok"));
	}
	
	//	 @Test(expected = UnsupportedOperationException.class)
	//	 public void typecastPutTest() {
	//	 	assertEquals("", genericConvertMap.typecastPut("my_key", "my_value"));
	//	 }
	
	// @Test
	// public void typecastPutValidTest() {
	// 	when(proxyMap.typecastPut("my_key", "my_value")).thenCallRealMethod();
	// 	when(proxyMap.get("my_key")).thenReturn("1");
	// 	when(proxyMap.put("my_key", "my_value")).thenReturn("my_value");
	// 	assertEquals("my_value", proxyMap.typecastPut("my_key", "my_value"));
	// }
	// 
	//	 @Test(expected = UnsupportedOperationException.class)
	//	 public void convertPutTest() {
	//	 	assertEquals("", unsupported.convertPut("my_key", "my_value"));
	//	 }
	
	// @Test(expected = UnsupportedOperationException.class)
	// public void convertPutOverloadTest() {
	// 	assertEquals("", genericConvertMap.convertPut("my_key", "my_value", String.class));
	// }
	// 
	// @Test
	// public void convertPutOverloadValidTest() {
	// 	when(proxyMap.convertPut("my_key", "my_value", String.class)).thenCallRealMethod();
	// 	when(proxyMap.get("my_key")).thenReturn("my_value");
	// 	when(proxyMap.put("my_key", "my_key")).thenReturn("my_key");
	// 	assertEquals("my_key", proxyMap.convertPut("my_key", "my_value", String.class));
	// }
}
