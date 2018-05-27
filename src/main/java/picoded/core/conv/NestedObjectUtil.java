package picoded.core.conv;

import java.util.*;

import picoded.core.exception.ExceptionMessage;

/**
 * Utility class which handles various manipulation of nested objects, 
 * such as a Map/List.
 **/
public class NestedObjectUtil {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected NestedObjectUtil() {
		throw new IllegalAccessError( ExceptionMessage.staticClassConstructor );
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Deep cloning utility function.
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Does a deep cloning of a provided object
	 * and its value, and returns it. 
	 * 
	 * The output will be generalized into its respective
	 * types, of map / list / array implmentation.
	 * 
	 * This is designed to support common java standard class
	 * types, such as Map, List, Set, and known primitives
	 * 
	 * @TODO : Proper arbitary aray support?
	 * 
	 * @param  input value to detach from
	 * 
	 * @return  datached value to return
	 */
	static public Object deepCopy(Object in) {

		// Null clones to null
		if( in == null ) {
			return null;
		}

		// Array cloning
		if( in.getClass().isArray() ) {

			// Clone as a primitive array if possible, 
			// and return the result if its valid
			//
			// This only support the following : 
			// int[], long[], short[], float[], double[], byte[], char[]
			Object ret = ArrayConv.clonePrimitiveArray(in);
			if( ret != null ) {
				return ret;
			}

			//
			// At this point array is assumed to be an Object[]
			// and is cloned as such.
			//
			Object[] source = (Object[])in;
			Object[] result = new Object[source.length];
			for( int i=0; i<source.length; ++i) {
				result[i] = deepCopy(source[i]);
			}
		}

		// Set conversion
		if( in instanceof Set ) {
			// Return hashset
			HashSet<Object> ret = new HashSet<>();

			// For each value in original set
			for (Object item : ((Set<Object>)in) ) {
				ret.add(item);
			}

			// Return the cloned set
			return ret;
		}

		// List conversion
		if( in instanceof List ) {
			// Return List
			ArrayList<Object> ret = new ArrayList<>();

			// For each item in list
			for(Object item : ((List<Object>)in) ) {
				ret.add(item);
			}

			// Return the cloned list
			return ret;
		}

		// Map conversion
		if( in instanceof Map ) {
			// Return map
			Map<Object,Object> ret = new HashMap<>();

			// For each item in map
			for( Map.Entry<Object,Object> pair : ((Map<Object,Object>)in).entrySet() ) {
				ret.put( pair.getKey(), pair.getValue() );
			}

			// Return the cloned map
			return ret;
		}

		// Final fallback using JSON conversion
		return ConvertJSON.toObject(ConvertJSON.fromObject(in));
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Filtered key set handling
	//
	//--------------------------------------------------------------------------------------------------
	

	/**
	 * First level keySet fetching
	 * 
	 * @param  inMap input map to fetch and filter the keyset
	 * 
	 * @return  keyset that is filtered to one "level"
	 **/
	public static <V> Set<String> filterKeySet(Map<String,V> inMap) {
		return filterKeySet(inMap.keySet());
	}
	
	/**
	 * First level keySet fetching
	 * 
	 * @param  inSet raw keyset representing a map
	 * 
	 * @return  keyset that is filtered to one "level"
	 **/
	public static Set<String> filterKeySet(Set<String> inSet) {
		// Return key set when completed
		HashSet<String> ret = new HashSet<String>();
		
		// Iterate across all the ConfigFile items : and populate the result
		// with only the first level keys
		for (String key : inSet) {
			String keyString = key.toString();
			String[] splitKeyString = NestedObjectFetch.splitObjectPath(keyString);
			ret.add(splitKeyString[0]);
		}
		
		// Return compiled keyset
		return ret;
	}
	

	//--------------------------------------------------------------------------------------------------
	//
	// Fully Qualified Name unpacking
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Takes in a map,
	 * search its keys for Fully Qualified Name formatting.
	 * Unpack the various key / values related to it.
	 *
	 * So for example,
	 *
	 * `{ "a[0].b" : "hello" }`
	 *
	 * Will unpack to
	 * ```
	 *	{
	 *		"a" : [
	 *			{
	 *				"b" : "hello"
	 *			}
	 *		]
	 *	}
	 * ```
	 *
	 * This Unpacks all keynames, and rewrites any underlying map/list implementation
	 * if needed. Note that while this helps normalize input parameters against a
	 * large collection of format interpretation, its implications are rarely well
	 * understood when things does not work as intended.
	 *
	 * @param inMap map to unpack and modify on
	 **/
	@SuppressWarnings("unchecked")
	public static <K, V> void unpackFullyQualifiedNameKeys(Map<K, V> inMap) {
		
		//
		// Normalize keyset, as modifications will occur
		// We do not want a modification access exception
		//
		Set<K> keys = new HashSet<K>(inMap.keySet());
		for (K key : keys) {
			
			// Get and process the key path
			String keyStr = GenericConvert.toString(key, "");
			String[] keyPath = NestedObjectFetch.splitObjectPath(keyStr);
			
			// Key path is considered "complex" and needs "unpacking"
			int keyLength = keyPath.length;
			if (keyLength > 1) {
				
				// Final value to actually store without unpacking
				Object value = inMap.get(keyStr);
				
				// Last index
				int lastIndex = keyLength - 1;
				
				// The "object" to start unpacking from
				Object base = inMap;
				
				// Start the key diving
				for (int i = 0; i < keyLength; ++i) {
					
					// Gets the next step of the path
					String keyItem = keyPath[i];
					
					// If last index. Time to finalize the object
					if (lastIndex == i) {
						MapOrListUtil.setValue(base, keyItem, value);
						break; // End key diving loop
					}
					
					// Or get the next base object
					Object newBase = MapOrListUtil.getValue(base, keyItem, null);
					
					// If base is null, generate it
					if (newBase == null) {
						// Grab next key to decide object type
						String nextKey = keyPath[i + 1];
						
						// Check if next key is numeric
						int nextKeyInt = GenericConvert.toInt(nextKey, -1);
						if (nextKeyInt >= 0) {
							// Numeric key : assume array
							newBase = new ArrayList<Object>();
						} else {
							// Non numeric key : assume map
							newBase = new HashMap<String, Object>();
						}
						
						MapOrListUtil.setValue(base, keyItem, newBase);
					}
					
					// Go one depth deeper, and try next layer
					base = newBase;
				}
				
				// Finnished key dive, delete old value
				inMap.remove(keyStr);
			}
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// Normalize object path, for case insensitive fetchNestedObject calls
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Takes a possibly case insensitive key, and normalize it to the actual key path (if found) for the selected object
	 *
	 * @param base  Map / List to manipulate from
	 * @param key   The input key to fetch, possibly nested
	 *
	 * @return  The normalized key
	 **/
	public static String normalizeObjectPath(Object base, String key) {
		return normalizeObjectPath(base, NestedObjectFetch.splitObjectPath(key, null), null).toString();
	}
	
	/**
	 * Takes a possibly case insensitive key, and normalize it to the actual key path (if found) for the selected object
	 *
	 * @param base          Map / List to manipulate from
	 * @param splitKeyPath  split Key Path
	 * @param res           String with existing value if available
	 * @return The normalized key
	 */
	@SuppressWarnings("unchecked")
	protected static StringBuilder normalizeObjectPath(Object base, List<String> splitKeyPath,
		StringBuilder res) {
		
		//
		// Result string builder setup
		//--------------------------------------------------------
		
		if (res == null) {
			res = new StringBuilder();
		}
		
		//
		// End of path recursion
		//--------------------------------------------------------
		
		/**
		 * No additional parts, return existing path
		 **/
		if (splitKeyPath == null || splitKeyPath.isEmpty()) {
			return res;
		}
		
		//
		// Get base respective array or map type
		//--------------------------------------------------------
		
		// Base to map / list conversion
		Map<String, Object> baseMap = null;
		List<Object> baseList = null;
		
		Object obj = GenericConvert.resolvedListOrMap(base);
		if (obj != null) {
			if (obj instanceof Map) {
				baseMap = (Map<String, Object>) obj;
			} else {
				baseList = (List<Object>) obj;
			}
		}
		
		if (baseMap == null && baseList == null) {
			throw new RuntimeException("Unexpected key path format : "
				+ ConvertJSON.fromList(splitKeyPath));
		}
		
		//
		// Process if its map or list respectively
		//--------------------------------------------------------
		
		String currentKey = splitKeyPath.get(0);
		List<String> nextKeyPathSet = splitKeyPath.subList(1, splitKeyPath.size());
		Object subObject = MapOrListUtil.getValue(base, currentKey);
		
		// Failed fetch with currentKey
		if (subObject == null) {
			
			// Fails if its an array : no such thing as case insensitive number
			if (baseList != null) {
				return new StringBuilder();
			}
			
			//System.out.println("normalize search - "+currentKey);
			//System.out.println("normalize keySet - "+baseMap.keySet());
			
			// Attempt to correct the case insensitivity issue
			for (String oneKey : baseMap.keySet()) {
				if (oneKey.equalsIgnoreCase(currentKey)) {
					currentKey = oneKey;
					subObject = baseMap.get(currentKey);
					if (subObject != null) {
						break;
					}
				}
			}
			
			// Still invalid after search
			if (subObject == null) {
				return new StringBuilder();
			}
		}
		
		if (baseMap != null) {
			//
			// Base is a map
			//
			if (res.length() > 0) {
				res.append(".");
			}
			res.append(currentKey);
			
		} else { // if (baseList != null) { //baseList is never null here
			//
			// Base is a list
			//
			res.append("[");
			res.append(currentKey);
			res.append("]");
		}
		
		// Terminate when done
		if (nextKeyPathSet.isEmpty()) {
			return res;
		}
		
		// Else recursive fetch
		return normalizeObjectPath(subObject, nextKeyPathSet, res);
	}
}
