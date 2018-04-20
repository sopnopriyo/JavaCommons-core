package picoded.core.struct.aggregation.mapreduce;

import java.math.BigDecimal;
import java.util.Map;

import picoded.core.conv.GenericConvert;
import picoded.core.conv.NestedObject;

/**
 * Internal representation of a simple MapReduceBase class,
 *
 * This forms the base class, reuse for sum, avg, etc.
 * that is easily extendable. for usage in "Aggregation" class
 */
public abstract class MapReduceBase {

	//------------------------------------------------------
	//
	//  Commoon functionality that is overwritten
	//
	//------------------------------------------------------

	/**
	 * Result variable to return computation on (by default)
	 */
	protected BigDecimal res = new BigDecimal(0);

	/**
	 * mapping function used to process a single value as it comes in
	 * 
	 * @param   value used in mapping, this value is possible null if the parameter does not exist
	 */
	public abstract void map(BigDecimal val);

	/**
	 * mapping function used to process a parameter in a map
	 * 
	 * @param  inmap map object, to extract the value from
	 * @param  key   to extract value from
	 */
	public void map(Object inmap, String key) {
		Object valObj = NestedObject.fetchNestedObject(inmap, key);
		BigDecimal val = GenericConvert.toBigDecimal(valObj, null);
		map(val);
	}

	//------------------------------------------------------
	//
	//  Default blank constructor
	//
	//------------------------------------------------------

	/**
	 * Define a blank constructor, because it is a blank constructor
	 * this should not be used directly, but part of the Aggregation Class
	 **/
	public MapReduceBase() {
		// does nothing
	}
	
	//------------------------------------------------------
	//
	//  Common functionality that is reused
	//
	//------------------------------------------------------

	/**
	 * mapping function used to process a parameter in a map
	 * This is a convinence function for type hinting, for Map<String,Object>
	 * 
	 * @param  inmap map object, to extract the value from
	 * @param  key   to extract value from
	 */
	public void map(Map<String,Object> inmap, String key) {
		map((Object)inmap, key);
	}

	/**
	 * Returns the current calculation reduce-ed as a BigDecimal.
	 * By default this simply returns a clone of the "res" object
	 * 
	 * @return  BigDecimal representing the current result
	 */
	public BigDecimal reduce() {
		BigDecimal ret = new BigDecimal(0);
		ret.add(res);
		return ret;
	}

	/**
	 * Reset any running calculation, used to reset existing map operations 
	 */
	public void reset() {
		res = new BigDecimal(0);
	}

	/**
	 * Duplicate a "blank" instance of the current class object
	 * Used to initialize calculators for each "run"
	 * 
	 * @return MapReduceBase instance clone
	 **/
	public MapReduceBase newInstance() {
		try {
			Class<? extends MapReduceBase> classObj = this.getClass();
			return classObj.newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}