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
	public void map(BigDecimal val) {
		// Initialize the res variable (if needed)
		if( res == null ) {
			res = val;
		} else if( val != null ) {
			// Return the max of the res / value
			res = res.max(val);
		}
	}
}