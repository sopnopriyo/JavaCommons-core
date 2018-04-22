package picoded.core.struct.query.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import picoded.core.struct.query.Aggregation;

/**
 * Utility interface, to provide a standardised interface to query
 * Collection for various results.
 * 
 * Ideally this should be optimized for the relevent backend.
 */
public interface CollectionQueryInterface <V extends Map<String,Object>> {

	// Query operations (to optimize on specific implementation)
	//--------------------------------------------------------------------------
	
	/**
	 * Performs a search query, and returns the respective DataObjects
	 *
	 * @param   where query statement
	 * @param   where clause values array
	 *
	 * @return  The DataObject[] array
	 **/
	default V[] query(String whereClause, Object[] whereValues) {
		return query(whereClause, whereValues, null, 0, 0);
	}
	
	/**
	 * Performs a search query, and returns the respective DataObjects
	 *
	 * @param   where query statement
	 * @param   where clause values array
	 * @param   query string to sort the order by, use null to ignore
	 *
	 * @return  The DataObject[] array
	 **/
	default V[] query(String whereClause, Object[] whereValues, String orderByStr) {
		return query(whereClause, whereValues, orderByStr, 0, 0);
	}
	
	/**
	 * Performs a search query, and returns the respective DataObjects
	 *
	 * @param   where query statement
	 * @param   where clause values array
	 * @param   query string to sort the order by, use null to ignore
	 * @param   offset of the result to display, use -1 to ignore
	 * @param   number of objects to return max, use -1 to ignore
	 *
	 * @return  The DataObject[] array
	 **/
	V[] query(String whereClause, Object[] whereValues, String orderByStr,
		int offset, int limit);
	
	/**
	 * Performs a search query, and returns the respective DataObjects
	 *
	 * @param   where query statement
	 * @param   where clause values array
	 *
	 * @return  The total count for the query
	 **/
	default long queryCount(String whereClause, Object[] whereValues) {
		return query(whereClause, whereValues).length;
	}

	// Aggregation operations (to optimize on specific implementation)
	//--------------------------------------------------------------------------
	
	default BigDecimal[] aggregation(
		String[] aggregationTerms, 
		String whereClause, 
		Object[] whereValues
	) {
		// 1. Initialize the aggregation object (fail fast)
		Aggregation agg = Aggregation.build(aggregationTerms);

		// 2. Get the query result, as a collection
		V[] resArray = query(whereClause, whereValues);
		List<Object> resCollection = (List<Object>)(List<?>)Arrays.asList(resArray);

		// 3. compute the aggregation (in a single pass)
		return agg.compute(resCollection);
	}
	
}