package picoded.core.conv;

//Target test class
import static org.junit.Assert.*;
import static picoded.core.conv.NestedObjectUtil.*;

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
 * Test suite to verify the methods inside NestedObjectUtil
 */
public class NestedObjectUtil_test {

	//--------------------------------------------------------------------------------------------------
	//
	// Deep cloning testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void DeepCopy_string_test() {
		assertEquals("hello", NestedObjectUtil.deepCopy("hello"));
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Normalize object path testing
	//
	//--------------------------------------------------------------------------------------------------
	
	@Test
	public void normalizeObjectPathTest() {
		assertEquals("", normalizeObjectPath(null, null));
		
		assertEquals("", normalizeObjectPath(null, null, null).toString());
		StringBuilder sb = new StringBuilder();
		assertEquals("", normalizeObjectPath(null, null, sb).toString());
		List<String> list = new ArrayList<String>();
		assertEquals("", normalizeObjectPath(null, list, sb).toString());
		list.add("key");
		List<String> base = new ArrayList<String>();
		assertEquals("", normalizeObjectPath(base, list, sb).toString());
		base.add("my_path");
		assertEquals("", normalizeObjectPath(base, list, sb).toString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", "my_path");
		assertEquals("key", normalizeObjectPath(map, list, sb).toString());
		// Attempt to correct the case insensitivty issue
		list = new ArrayList<String>();
		list.add("KEY");
		sb = new StringBuilder();
		map = new HashMap<String, Object>();
		map.put("key", "my_path");
		assertEquals("key", normalizeObjectPath(map, list, sb).toString());
		// Attempt to correct the case insensitivty issue
		list = new ArrayList<String>();
		list.add("KEY");
		sb = new StringBuilder();
		map = new HashMap<String, Object>();
		map.put("key", "my_path");
		assertEquals("key", normalizeObjectPath(map, list, sb).toString());
		// Attempt to correct the case insensitivty issue
		list = new ArrayList<String>();
		list.add("KEEY");
		sb = new StringBuilder();
		map = new HashMap<String, Object>();
		map.put("key", "my_path");
		assertEquals("", normalizeObjectPath(map, list, sb).toString());
		list = new ArrayList<String>();
		list.add("0");
		sb = new StringBuilder();
		assertEquals("[0]", normalizeObjectPath(base, list, sb).toString());
		// Attempt to correct the case insensitivty issue
		list = new ArrayList<String>();
		list.add("KEY");
		sb = new StringBuilder();
		map = new HashMap<String, Object>();
		map.put("key", null);
		assertEquals("", normalizeObjectPath(map, list, sb).toString());
		// Attempt to correct the case insensitivty issue
		list = new ArrayList<String>();
		list.add("KEY");
		//sb = new StringBuilder();
		map = new HashMap<String, Object>();
		map.put("key", "my_path");
		sb.append("previous");
		assertEquals("previous.key", normalizeObjectPath(map, list, sb).toString());
	}
	
	@Test(expected = RuntimeException.class)
	public void normalizeObjectPathRecursiveExceptionTest() {
		// Else recursive fetch
		List<String> list = new ArrayList<String>();
		list.add("KEY");
		list.add("key1");
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", "my_path");
		map.put("key1", "my_path1");
		assertEquals("previous.key", normalizeObjectPath(map, list, sb).toString());
	}
	
	@Test
	public void normalizeObjectPathRecursiveTest() {
		// Else recursive fetch
		List<String> list = new ArrayList<String>();
		list.add("key1");
		list.add("key2");
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put("s_key1", "s_my_path1");
		subMap.put("s_key2", "s_my_path2");
		map.put("key1", subMap);
		map.put("key2", "my_path2");
		assertEquals("", normalizeObjectPath(map, list, sb).toString());
	}
	
	@Test(expected = RuntimeException.class)
	public void normalizeObjectPathExceptionTest() {
		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<String>();
		list.add("key");
		assertEquals("", normalizeObjectPath(null, list, sb).toString());
	}
	
	@Test(expected = RuntimeException.class)
	public void normalizeObjectPathRecursiveException1Test() {
		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<String>();
		list.add("0");
		list.add("1");
		List<String> base = new ArrayList<String>();
		base.add("my_path");
		base.add("my_second_path");
		sb = new StringBuilder();
		assertEquals("[0]", normalizeObjectPath(base, list, sb).toString());
	}
	
}