package picoded.core.struct;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import picoded.core.struct.template.StandardArrayList_test;

//
// @TODO : assertNull is not a proper validation test - to do beyond that
//
public class GenericConvertList_test extends StandardArrayList_test {
	
	class GenericConvertTest<E> implements GenericConvertList<E> {
		
	}
	
	class ProxyTest<E> implements GenericConvertList<E> {
		ArrayList<E> base = new ArrayList<E>();
		
		@Override
		public int size() {
			return base.size();
		}
		
		// Implementation to actual base list
		@Override
		public void add(int index, E value) {
			base.add(index, value);
		}
		
		// Implementation to actual base list
		@Override
		public E remove(int index) {
			return base.remove(index);
		}
		
		// Implementation to actual base list
		@Override
		public E get(int key) {
			return base.get(key);
		}
	}
	
	GenericConvertList<Object> unsupported = null;
	ProxyTest<Object> proxyList = null;
	
	@Override
	@Before
	public void setUp() {
		unsupported = new GenericConvertTest<>();
		list = new ProxyTest<Object>();
		proxyList = new ProxyTest<Object>();
	}
	
	@Override
	@After
	public void tearDown() {
		unsupported = null;
		list = null;
		proxyList = null;
	}
	
	@Test
	public void notNullTest() {
		assertNotNull(unsupported);
		assertNotNull(list);
		assertNotNull(proxyList);
	}
	
	//
	// Unsupported Operation Exception
	//
	
	@Test(expected = UnsupportedOperationException.class)
	public void getUnsupportedTest() {
		unsupported.get(0);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void addUnsupportedTest() {
		unsupported.add(1, "value");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void removeUnsupportedTest() {
		unsupported.remove("key");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void removeWithParamIntUnsupportedTest() {
		unsupported.remove(1);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void sizeUnsupportedTest() {
		unsupported.remove("key");
	}
	
	@Test
	public void getBooleanTest() {
		proxyList.add(0, "value");
		proxyList.getBoolean(0);
	}
	
	@Test
	public void getSubtleTest() {
		assertNull(proxyList.getSubtle(-1));
		assertNull(proxyList.getSubtle(0));
		assertNull(proxyList.getSubtle(1));
		assertNull(proxyList.getSubtle(2));
	}
	
	@Test
	public void getSubtleNonZeroTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals("me", proxyList.getSubtle(1));
	}
	
	//
	// GET conversion test
	//
	
	@Test
	public void getStringTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals("me", proxyList.getString(1));
	}
	
	@Test
	public void getString2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals("ok", proxyList.getString(1, "ok"));
	}
	
	@Test
	public void getBoolean2ParamTest() {
		proxyList.add("value");
		proxyList.add("false");
		assertEquals(false, proxyList.getBoolean(1, true));
	}
	
	@Test
	public void getNumberTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(BigDecimal.valueOf(1), proxyList.getNumber(1));
	}
	
	@Test
	public void getNumber2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(BigDecimal.valueOf(1), proxyList.getNumber(1, -1));
	}
	
	@Test
	public void getIntTest() {
		assertEquals(0, proxyList.getInt(1));
	}
	
	@Test
	public void getInt2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.getInt(1, -1));
	}
	
	@Test
	public void getLongTest() {
		assertEquals(0, proxyList.getLong(1));
	}
	
	@Test
	public void getFloatTest() {
		assertEquals(0, proxyList.getFloat(1), 0.01);
	}
	
	@Test
	public void getDoubleTest() {
		assertEquals(0, proxyList.getDouble(1), 0.01);
	}
	
	@Test
	public void getDouble2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1.0, proxyList.getDouble(1, -1l), 0.01);
	}
	
	@Test
	public void getByteTest() {
		assertEquals(0, proxyList.getByte(1));
	}
	
	@Test
	public void getByte2ParamTest() {
		byte temp = 2;
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.getByte(1, temp));
	}
	
	@Test
	public void getShortTest() {
		assertEquals(0, proxyList.getShort(1));
	}
	
	@Test
	public void getShort2ParamTest() {
		short temp = 2;
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.getShort(1, temp));
	}
	
	@Test
	public void getUUIDTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertNull(proxyList.getUUID(1));
	}
	
	@Test
	public void getUUID2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getUUID(1, "ok"));
	}
	
	@Test
	public void getGUIDTest() {
		proxyList.add("value");
		proxyList.add("123456789o123456789o12");
		assertEquals("123456789o123456789o12", proxyList.getGUID(1));
	}
	
	@Test
	public void getObjectListTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertNull(proxyList.getObjectList(1));
	}
	
	@Test
	public void getStringArray2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getStringArray(1, "ok"));
	}
	
	@Test
	public void getObjectArrayTest() {
		assertNull(proxyList.getObjectArray(1));
	}
	
	@Test
	public void getObjectArray2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getObjectArray(1, "ok"));
	}
	
	@Test
	public void getGenericConvertStringMapTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getGenericConvertStringMap(1));
	}
	
	@Test
	public void getGenericConvertStringMap2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getGenericConvertStringMap(1, "ok"));
	}
	
	@Test
	public void getGenericConvertListTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getGenericConvertList(0));
	}
	
	@Test
	public void getGenericConvertListSecondTest() {
		proxyList.add("value");
		assertNull(proxyList.getGenericConvertList(1));
	}
	
	@Test
	public void getGenericConvertList2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.getGenericConvertList(1, "ok"));
	}
	
	@Test
	public void getStringArrayTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertNull(proxyList.getStringArray(1));
	}
	
	@Test
	public void getGUID2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals("123456789o123456789o12", proxyList.getGUID(1, "123456789o123456789o12"));
	}
	
	@Test
	public void getFloat2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals(-1.0, proxyList.getFloat(1, -1f), 0.01);
	}
	
	@Test
	public void getObjectList2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertArrayEquals(new Object[] { "1", 1 }, proxyList
			.getObjectList(1, new Object[] { "1", 1 }).toArray());
	}
	
	@Test
	public void getLong2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.getLong(1, -1l));
	}
	
	@Test
	public void getLong2ParamAlternateTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals(-5l, proxyList.getLong(1, -5l));
	}
	
	//
	// FETCH conversion test
	//
	
	@Test
	public void fetchStringTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals("me", proxyList.fetchString("1"));
	}
	
	@Test
	public void fetchString2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals("ok", proxyList.fetchString("1", "ok"));
	}
	
	@Test
	public void fetchBoolean2ParamTest() {
		proxyList.add("value");
		proxyList.add("false");
		assertEquals(false, proxyList.fetchBoolean("1", true));
	}
	
	@Test
	public void fetchNumberTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(BigDecimal.valueOf(1), proxyList.fetchNumber("1"));
	}
	
	@Test
	public void fetchNumber2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(BigDecimal.valueOf(1), proxyList.fetchNumber("1", -1));
	}
	
	@Test
	public void fetchIntTest() {
		assertEquals(0, proxyList.fetchInt("1"));
	}
	
	@Test
	public void fetchInt2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.fetchInt("1", -1));
	}
	
	@Test
	public void fetchLongTest() {
		assertEquals(0, proxyList.fetchLong("1"));
	}
	
	@Test
	public void fetchFloatTest() {
		assertEquals(0, proxyList.fetchFloat("1"), 0.01);
	}
	
	@Test
	public void fetchDoubleTest() {
		assertEquals(0, proxyList.fetchDouble("1"), 0.01);
	}
	
	@Test
	public void fetchDouble2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1.0, proxyList.fetchDouble("1", -1l), 0.01);
	}
	
	@Test
	public void fetchByteTest() {
		assertEquals(0, proxyList.fetchByte("1"));
	}
	
	@Test
	public void fetchByte2ParamTest() {
		byte temp = 2;
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.fetchByte("1", temp));
	}
	
	@Test
	public void fetchShortTest() {
		assertEquals(0, proxyList.fetchShort("1"));
	}
	
	@Test
	public void fetchShort2ParamTest() {
		short temp = 2;
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.fetchShort("1", temp));
	}
	
	@Test
	public void fetchUUIDTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertNull(proxyList.fetchUUID("1"));
	}
	
	@Test
	public void fetchUUID2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchUUID("1", "ok"));
	}
	
	@Test
	public void fetchGUIDTest() {
		proxyList.add("value");
		proxyList.add("123456789o123456789o12");
		assertEquals("123456789o123456789o12", proxyList.fetchGUID("1"));
	}
	
	@Test
	public void fetchObjectListTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertNull(proxyList.fetchObjectList("1"));
	}
	
	@Test
	public void fetchStringArray2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchStringArray("1", "ok"));
	}
	
	@Test
	public void fetchObjectArrayTest() {
		assertNull(proxyList.fetchObjectArray("1"));
	}
	
	@Test
	public void fetchObjectArray2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchObjectArray("1", "ok"));
	}
	
	@Test
	public void fetchObject2ParamTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals("me", proxyList.fetchObject("1", "ok"));
	}
	
	@Test
	public void fetchGenericConvertStringMapTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchGenericConvertStringMap("1"));
	}
	
	@Test
	public void fetchGenericConvertStringMap2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchGenericConvertStringMap("1", "ok"));
	}
	
	@Test
	public void fetchGenericConvertListTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchGenericConvertList("0"));
	}
	
	@Test
	public void fetchGenericConvertListSecondTest() {
		proxyList.add("value");
		assertNull(proxyList.fetchGenericConvertList("1"));
	}
	
	@Test
	public void fetchGenericConvertList2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertNull(proxyList.fetchGenericConvertList("1", "ok"));
	}
	
	@Test
	public void fetchStringArrayTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertNull(proxyList.fetchStringArray("1"));
	}
	
	@Test
	public void fetchGUID2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals("123456789o123456789o12", proxyList.fetchGUID("1", "123456789o123456789o12"));
	}
	
	@Test
	public void fetchFloat2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertEquals(-1.0, proxyList.fetchFloat("1", -1f), 0.01);
	}
	
	@Test
	public void fetchObjectList2ParamTest() {
		proxyList.add("value");
		proxyList.add("ok");
		assertArrayEquals(new Object[] { "1", 1 }, proxyList
			.getObjectList(1, new Object[] { "1", 1 }).toArray());
	}
	
	@Test
	public void fetchObjectTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals("me", proxyList.fetchObject("1"));
	}
	
	@Test
	public void fetchLong2ParamTest() {
		proxyList.add("value");
		proxyList.add("1");
		assertEquals(1, proxyList.fetchLong("1", -1l));
	}
	
	@Test
	public void fetchLong2ParamAlternateTest() {
		proxyList.add("value");
		proxyList.add("me");
		assertEquals(-5l, proxyList.fetchLong("1", -5l));
	}
}
