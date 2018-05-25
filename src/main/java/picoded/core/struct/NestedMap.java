package picoded.core.struct;

import java.util.Map;

/**
 * Special varient of GenericConvertMap where get operations 
 * are performed in a recursive manner. 
 * 
 * Get operations are perform as if all key names were
 * flatten into FQDN key names.
 * 
 * Generally this should be used strictly only for read operations.
 * After the initial write operation setup due to its heavy performance penalties
 **/
public class NestedMap<K, V> extends GenericConvertConcurrentHashMap<K, V> {

	//------------------------------------------------------
	//
	// Constructors
	//
	//------------------------------------------------------
	
	/**
	 * Constructor
	 **/
	public NestedMap() {
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param  map data used to initialize the class
	 **/
	public NestedMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	//-----------------------------------------------------------------------------------
	//
	// KeySet handling
	//
	//-----------------------------------------------------------------------------------
	
	// /**
	//  * @return the top layer keySet, excluding any nested key paths
	//  **/
	// public Set<String> keySet() {
	// 	// The return result keySet
	// 	HashSet<String> ret = new HashSet<String>();
		
	// 	// Iterate across all the key set
	// 	// For the top level keyset
	// 	for (String key : super.keySet()) {
	// 		String keyString = key.toString();
	// 		String[] splitKeyString = keyString.split("\\.");
	// 		ret.add(splitKeyString[0]);
	// 	}
		
	// 	return ret;
	// }
	
	//-----------------------------------------------------------------------------------
	//
	// Get request handling
	//
	//-----------------------------------------------------------------------------------
	
	/**
	 * When a get request is called here, it attempts to pull from the resepective sub map, by splitting the request key.
	 *
	 * For example: config.main.header.test
	 *
	 * Will be split as "config.main.header", and "test",
	 * It will then attempt to fetch from the "config.main.header" file if it exists.
	 *
	 * If it fails to find, it will then resepectively search 1 level higher
	 * Splitting it as followed "config.main", and "header.test"
	 **/
	/*
	public Object get(Object key) {
		String keyString = key.toString();
		String[] splitKeyString = keyString.split("\\.");
		
		// an issue could arise if there are conflicting keys
		// example
		// <a.b.c, <d, e>> //json
		// <a.b, <c.d, e>> //ini file
		// in this case, passing a key of "a.b.c.d" will always hit the json
		// file first, which might not be intended.
		// having nonconflicting keys will avoid this, but this is just a heads
		// up
		for (int splitPt = splitKeyString.length; splitPt > 0; --splitPt) {
			String fileKey = StringUtils.join(ArrayUtils.subarray(splitKeyString, 0, splitPt), ".");
			String headerKey = StringUtils.join(
				ArrayUtils.subarray(splitKeyString, splitPt, splitKeyString.length), ".");
			
			Object returnVal = getExact(fileKey, headerKey);
			
			if (returnVal != null) {
				return returnVal;
			}
		}
		
		//
		// Attempts to get a submap if possible, else returns null
		//
		return getCachedSubMap(keyString);
	}
	*/

}