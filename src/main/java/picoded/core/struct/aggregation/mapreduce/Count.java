package picoded.core.struct.aggregation.mapreduce;

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
	public void map(BigDecimal val) {
		// Initialize the res variable (if needed)
		if( res == null ) {
			res = BigDecimal.ZERO;
		}

		// Increment the count when called
		if( val != null ) {
			res = res.add( BigDecimal.ONE );
		}
	}
}