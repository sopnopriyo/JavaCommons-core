package picoded.core.struct.query.mapreduce;

import java.math.BigDecimal;

/**
 * Avg function for MapReduceBase
 **/
public class Avg extends MapReduceBase {
	
	// this variable is here  to make the avg function not crash 
	// if the result has a repeating non terminating decimal portion
	// will limit the result to ~ 24 decimal places
	//
	// Note : precision vs scale terminology - https://stackoverflow.com/questions/35435691/bigdecimal-precision-and-scale
	private static int AVG_RESULT_MAX_SCALE = 24;
	
	/**
	 * Counting variable to use
	 */
	protected BigDecimal count = null;
	
	/**
	 * mapping function used to process a single value as it comes in
	 * 
	 * @param   value used in mapping, this value is possible null if the parameter does not exist
	 */
	public void map(BigDecimal val, Object rawVal) {
		// Add up those value if its provided
		if (val != null) {
			
			// Initialize the res variable (if needed)
			if (res == null) {
				res = BigDecimal.ZERO;
				count = BigDecimal.ZERO;
			}
			
			// Increment the value
			res = res.add(val);
			count = count.add(BigDecimal.ONE);
		}
	}
	
	/**
	 * Returns the current calculation reduce-ed as a BigDecimal.
	 * By default this simply returns a clone of the "res" object
	 * 
	 * @return  BigDecimal representing the current result
	 */
	public BigDecimal reduce() {
		return res.divide(count, AVG_RESULT_MAX_SCALE, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Reset any running calculation, used to reset existing map operations 
	 */
	public void reset() {
		res = null;
		count = null;
	}
	
}