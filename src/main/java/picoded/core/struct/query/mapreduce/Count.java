package picoded.core.struct.query.mapreduce;

import java.math.BigDecimal;

/**
 * Count function for MapReduceBase
 **/
public class Count extends MapReduceBase {
	/**
	 * mapping function used to process a single value as it comes in
	 * 
	 * @param   value used in mapping, this value is possible null if the parameter does not exist
	 */
	public void map(BigDecimal val, Object rawVal) {
		// Increment the count when called
		// only if a value is passed
		if (rawVal != null) {
			// Initialize the res variable (if needed)
			if (res == null) {
				res = BigDecimal.ONE;
			} else {
				res = res.add(BigDecimal.ONE);
			}
		}
	}
	
	/**
	 * mapping function used to process a parameter in a map
	 * 
	 * @param  inmap map object, to extract the value from
	 * @param  key   to extract value from
	 */
	public void map(Object inmap, String key) {
		// Wildcard matching
		if (key.equalsIgnoreCase("*")) {
			map(BigDecimal.ONE, BigDecimal.ONE);
		} else {
			super.map(inmap, key);
		}
	}
	
}