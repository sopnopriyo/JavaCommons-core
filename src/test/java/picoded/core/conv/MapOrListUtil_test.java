package picoded.core.conv;

//Target test class
import static org.junit.Assert.*;
import picoded.core.conv.MapOrListUtil;

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

/**
 * Test suite to verify the methods inside MapOrListUtil
 */
public class MapOrListUtil_test {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchObjectTest() {
		assertNull(MapOrListUtil.getValue(null, null, null));
		assertEquals("default", MapOrListUtil.getValue(null, null, "default"));
		Map map = new HashMap();
		assertNull(MapOrListUtil.getValue(map, null, null));
		map = new HashMap();
		map.put("key", "value");
		assertNull(MapOrListUtil.getValue(map, null, null));
		assertEquals("value", MapOrListUtil.getValue(map, "key", null));
		
		List list = new ArrayList();
		assertNull(MapOrListUtil.getValue(list, null, null));
		list = new ArrayList();
		list.add("value");
		assertNull(MapOrListUtil.getValue(list, null, null));
		assertNull("value", MapOrListUtil.getValue(list, "key", null));
		assertEquals("value", MapOrListUtil.getValue(list, "0", null));
		
		assertEquals("default", MapOrListUtil.getValue("value", "0", "default"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchObjectWithTwoParametersTest() {
		assertNull(MapOrListUtil.getValue(null, null));
		Map map = new HashMap();
		assertNull(MapOrListUtil.getValue(map, null));
		map = new HashMap();
		map.put("key", "value");
		assertNull(MapOrListUtil.getValue(map, null));
		assertEquals("value", MapOrListUtil.getValue(map, "key"));
		
		List list = new ArrayList();
		assertNull(MapOrListUtil.getValue(list, null));
		list = new ArrayList();
		list.add("value");
		assertNull(MapOrListUtil.getValue(list, null));
		assertNull("value", MapOrListUtil.getValue(list, "key"));
		assertEquals("value", MapOrListUtil.getValue(list, "0"));
		
		assertNull(MapOrListUtil.getValue("value", "0"));
	}
	
}