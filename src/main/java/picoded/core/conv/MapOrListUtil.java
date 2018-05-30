package picoded.core.conv;

import java.util.*;

/**
 * Utility library used to interact against either
 * a map or list object using a single set of api interface.
 * 
 * This is useful if the object provided is not known to be
 * either a map or list in advance.
 **/
public class MapOrListUtil {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected MapOrListUtil() {
		throw new IllegalAccessError("Utility class");
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Map / List type validation
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * @param  in object to validate if its either map or list
	 * @return true if object is either a map or list 
	 */
	public static boolean isValid(Object in) {
		if(in instanceof Map || in instanceof List) {
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Map / List set value utility functions
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Set a key-value pair inside Map, or List. This is used for convinence, when the map/list object type is unknown.
	 * 
	 * For example the following are valid varients
	 * 
	 * ```
	 * // Typically how you set a map, or list values
	 * mapObj.set("hello","world");
	 * listObj.add("something");
	 * listObj.set(0, "overwrite-value");
	 * 
	 * // Alternatively with MapOrListUtil.setValue
	 * MapOrListUtil.setValue(mapObj, "hello", "world");
	 * MapOrListUtil.setValue(listObj, "1", "something");
	 * MapOrListUtil.setValue(listObj, "0", "overwrite-value");
	 * ```
	 *
	 * @param  inObj, of Map / List to add to
	 * @param  key value as a string
	 * @param  value to insert
	 *
	 * @return  The Map, or List object, in its converted form (relevent if it was originally a string JSON)
	 **/
	@SuppressWarnings("unchecked")
	public static Object setValue(Object inObj, String key, Object value) {
		
		// Start by setting the value
		// in optimistic ideal scenerios
		//------------------------------------------------
		
		// Try converting to a map
		if (inObj instanceof Map) {
			// Map found, converting and inserting
			Map<String, Object> inMap = (Map<String, Object>) inObj;
			inMap.put(key, value);
			return inMap;
		}
		
		// Try converting to a list
		if (inObj instanceof List) {
			// List found, converting and inserting
			List<Object> inList = (List<Object>) inObj;
			
			// Convert key
			int idx = GenericConvert.toInt(key, -1);
			
			// Invalid key exception
			if (idx < 0) {
				throw new RuntimeException("Unexpected key to insert to List : " + key);
			}
			
			if (idx >= inList.size()) {
				inList.add(value);
			} else {
				inList.set(idx, value);
			}
			return inList;
		}
		
		// Ok sadly, the optimistic methods failed =(
		//------------------------------------------------
		
		// Time to go aggressive, and try again as a map
		Map<String, Object> tryMap = GenericConvert.toStringMap(inObj, null);
		if (tryMap != null) {
			return MapOrListUtil.setValue(tryMap, key, value);
		}
		
		// or as a list
		List<Object> tryList = GenericConvert.toList(inObj, null);
		if (tryList != null) {
			return MapOrListUtil.setValue(tryList, key, value);
		}
		
		// Q_Q all failed, time to bail
		//------------------------------------------------
		throw new RuntimeException("Unexpected object to set value on (it is neither a map, nor list)");
	}
	
	/**
	 * Gets an Object from either a Map, or a List. By attempting to use the provided key.
	 *
	 * This attempts to use the key AS IT IS. Only converting it to an int for List if needed.
	 * It does not do recursive fetch, if that is needed see `fetchNestedObject`
	 *
	 * For example the following are valid varients
	 * 
	 * ```
	 * // Typically how you set a map, or list values
	 * mapObj.get("hello");
	 * listObj.get(1);
	 * listObj.get(0);
	 * 
	 * // Alternatively with MapOrListUtil.getValue
	 * MapOrListUtil.getValue(mapObj, "hello");
	 * MapOrListUtil.getValue(listObj, "1");
	 * MapOrListUtil.getValue(listObj, "0");
	 * ```
	 *
	 * @param base      Map / List to manipulate from
	 * @param key       The input key to fetch, possibly nested
	 * @param fallback  The fallback default (if not convertable)
	 *
	 * @return         The fetched object, always possible unless fallbck null
	 **/
	@SuppressWarnings("unchecked")
	public static Object getValue(Object base, String key, Object fallback) {
		
		// Base to map / list conversion
		Map<String, Object> baseMap = null;
		List<Object> baseList = null;
		
		// Convert base object to either a list or map
		Object obj = GenericConvert.resolvedListOrMap(base);
		if (obj != null) {
			if (obj instanceof Map) {
				baseMap = (Map<String, Object>) obj;
			} else {
				baseList = (List<Object>) obj;
			}
		}
		
		// Fail on getting base item
		if (baseMap == null && baseList == null) {
			return fallback;
		}
		
		// Reuse vars?
		Object ret = null;
		
		// Full key fetch
		if (baseMap != null) {
			ret = baseMap.get(key);
		} else { // if( baseList != null ) {
			int idxPos = GenericConvert.toInt(key, -1);
			if (idxPos >= 0 && idxPos < baseList.size()) {
				ret = baseList.get(idxPos);
			}
		}
		
		// Full key found
		if (ret != null) {
			return ret;
		}
		
		// Fallback
		return fallback;
	}
	
	/**
	 * Default Null fallback, for `MapOrListUtil.getValue(base, key, fallback)`
	 *
	 * @param base      Map / List to manipulate from
	 * @param key       The input key to fetch, possibly nested
	 *
	 * @return         The fetched object, always possible unless fallbck null
	 **/
	public static Object getValue(Object base, String key) {
		return getValue(base, key, null);
	}
	
}