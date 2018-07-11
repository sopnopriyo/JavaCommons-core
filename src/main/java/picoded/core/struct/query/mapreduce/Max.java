package picoded.core.struct.query.mapreduce;

import java.math.BigDecimal;

/**
 * Max function for MapReduceBase
 **/
public class Max extends MapReduceBase {
	/**
	 * mapping function used to process a single value as it comes in
	 * 
	 * @param   value used in mapping, this value is possible null if the parameter does not exist
	 */
	public void map(BigDecimal val, Object rawVal) {
		// Only perform an action if a valid value was passed
		if (val != null) {
			if (res == null) {
				// Initialize the res variable (if needed)
				res = val;
			} else {
				// Process and compare the res variable
				res = res.max(val);
			}
		}
	}
}