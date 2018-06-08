package picoded.core.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import picoded.core.conv.*;

/**
 * Provides a HashMap<K,A ArrayList<V>>,
 * with utility functions to edit records.
 **/
@SuppressWarnings("serial")
public class ArrayListMap<K, V> extends GenericConvertHashMap<K, ArrayList<V>> {

	/**
	 * "Serializable" classes should have a version id
	 **/
	private static final long serialVersionUID = 1L;
	
	/**
	 * Blank constructor
	 **/
	public ArrayListMap() {
		super();
	}

	//------------------------------------------------------------------------
	//
	//  Inner Array Setup
	//
	//------------------------------------------------------------------------

	/**
	 * Gets the sublist stored for a key.
	 * If it does not exists, it is initiated.
	 *
	 * @param  the key used
	 *
	 * @return Sublist used for the key
	 **/
	protected ArrayList<V> getSubList(K key) {
		ArrayList<V> ret = get(key);
		
		if (ret == null) {
			ret = new ArrayList<V>();
			put(key, ret);
		}
		
		return ret;
	}
	
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
	public void append(K key, V val) {
		getSubList(key).add(val);
	}
	
	/**
	 * Appends the value to the inner list, creating a new ArrayList if needed
	 *
	 * @param   key     key to use
	 * @param   value   values emuneration to append
	 *
	 * @return   returns itself
	 **/
	public void append(K key, Collection<V> values) {
		if (values == null) {
			return;
		}
		append(key, Collections.enumeration(values));
	}
	
	/**
	 * Appends the value to the inner list, creating a new ArrayList if needed
	 *
	 * @param   key     key to use
	 * @param   value   values emuneration to append
	 *
	 * @return   returns itself
	 **/
	public void append(K key, Enumeration<V> values) {
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
	public void appendIfNotExists(K key, V val) {
		ArrayList<V> subList = getSubList(key);
		if (!(subList.contains(val))) {
			subList.add(val);
		}
	}
	
	/**
	 * Returns the map object, type casted
	 * Following native Map/List standard
	 **/
	@SuppressWarnings("unchecked")
	public Map<K, List<V>> standardMap() {
		return (Map<K, List<V>>) (Object) this;
	}
	
	/**
	 * Implments a JSON to string conversion
	 **/
	@Override
	public String toString() {
		return GenericConvert.toString(this);
	}

	/**
	 * Returns a new map, with all the internal List<V> objects converted to V[] Array
	 *
	 * @return   the flatten map array
	 **/
	public Map<K, V[]> toMapArray(V[] arrayType) {
		return MapValueConv.listToArray(standardMap(), arrayType);
	}
	
}
