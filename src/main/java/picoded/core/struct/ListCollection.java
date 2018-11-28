package picoded.core.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import picoded.core.conv.*;
import picoded.core.struct.template.UnsupportedDefaultMap;

/**
 * Utility interface for a collection (with keynames) of list values, for example
 *
 * ```
 * public class CustomListMap extends GenericConvertHashMap<String,List<V>> implements ListCollection<String, V> {
 *     // ... class definition here ...
 * }
 *
 * public class CustomListMap extends GenericConvertArrayList<List<V>> implements ListCollection<int, V> {
 *     // ... class definition here ...
 * }
 * ```
 **/
public interface ListCollection<K, V> {
	
	//------------------------------------------------------------------------
	//
	//  Fetching a sublist to a given key value
	//
	//------------------------------------------------------------------------
	
	/**
	 * Fetch the sublist stored for a key.
	 * If it does not exists, it should be initialized, stored, and returned.
	 *
	 * This result of this function is used to build all the list manipulation commands
	 *
	 * @param  the key used
	 *
	 * @return Sublist used for the key
	 **/
	List<V> fetchSubList(K key);
	
	//------------------------------------------------------------------------
	//
	//  Appending to array
	//
	//------------------------------------------------------------------------
	
	/**
	 * Adds to the sublist associated to the key value
	 *
	 * @param  the key used
	 * @param  the value to store
	 **/
	default void append(K key, V val) {
		fetchSubList(key).add(val);
	}
	
	/**
	 * Appends the value to the inner list
	 *
	 * @param   key     key to use
	 * @param   value   values emuneration to append
	 **/
	default void append(K key, Collection<V> values) {
		if (values == null) {
			return;
		}
		append(key, Collections.enumeration(values));
	}
	
	/**
	 * Appends the value to the inner list
	 *
	 * @param   key     key to use
	 * @param   value   values emuneration to append
	 **/
	default void append(K key, Enumeration<V> values) {
		if (values == null) {
			return;
		}
		while (values.hasMoreElements()) {
			this.append(key, values.nextElement());
		}
	}
	
	/**
	 * Adds to the sublist associated to the key value,
	 * only if it does not exists (no duplicates)
	 *
	 * @param  the key used
	 * @param  the value to store
	 **/
	default void appendIfNotExists(K key, V val) {
		List<V> subList = fetchSubList(key);
		if (!(subList.contains(val))) {
			subList.add(val);
		}
	}
	
	// /**
	//  * Returns the map object, type casted
	//  * Following native Map/List standard
	//  **/
	// @SuppressWarnings("unchecked")
	// public Map<K, List<V>> standardMap() {
	// 	return (Map<K, List<V>>) (Object) this;
	// }
	
	// /**
	//  * Implments a JSON to string conversion
	//  **/
	// @Override
	// public String toString() {
	// 	return GenericConvert.toString(this);
	// }
	
	// /**
	//  * Returns a new map, with all the internal List<V> objects converted to V[] Array
	//  *
	//  * @return   the flatten map array
	//  **/
	// public Map<K, V[]> toMapArray(V[] arrayType) {
	// 	return MapValueConv.listToArray(standardMap(), arrayType);
	// }
	
}
