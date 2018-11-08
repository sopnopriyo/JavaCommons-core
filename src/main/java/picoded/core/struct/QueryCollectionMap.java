package picoded.core.struct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import picoded.core.struct.query.Query;
import picoded.core.struct.query.QueryUtils;

/**
 * Query and aggregation interface for a collection of maps
 */
public interface QueryCollectionMap<V extends Map> extends Collection<V> {

	//
	// Query command support
	//

	/**
	 * Performs a search query, and returns the respective value list.
	 * 
	 * Note : When extending this class, with custom query handlers (like mysql), you should only replace this function.
	 *
	 * @param   queryClause, of where query statement and value
	 * @param   orderByStr string to sort the order by, use null to ignore
	 * @param   offset of the result to display, use -1 to ignore
	 * @param   number of objects to return max, use -1 to ignore
	 *
	 * @return  filtered and sorted Value list
	 **/
	@SuppressWarnings("unchecked")
	default List<V> query(Query queryClause, String orderByStr, int offset, int limit) {
		// Get the collection as a filteredlist
		List<V> queryList = null;

		// If query object is null, just convert it
		if( queryClause == null ) {
			if( this instanceof List ) {
				queryList = (List<V>)(this);
			} else {
				queryList = new ArrayList<V>(this);
			}
		} else {
			// Else : Lets query it
			queryList = queryClause.search(this);
		}
		
		// Sort, and offset list, after the query
		return QueryUtils.sortAndOffsetList(queryList, orderByStr, offset, limit);
	}
	
	/**
	 * Performs a search query, and returns the respective value list.
	 *
	 * @param   where query statement
	 * @param   where clause values array
	 * @param   query string to sort the order by, use null to ignore
	 * @param   offset of the result to display, use -1 to ignore
	 * @param   number of objects to return max, use -1 to ignore
	 *
	 * @return  filtered and sorted Value list
	 **/
	default List<V> query(String whereClause, Object[] whereValues, String orderByStr, int offset, int limit) {
		// Query object to use
		Query queryObj = null;

		// Where clause to convert to query object
		if( whereClause != null ) {
			queryObj = Query.build(whereClause, whereValues);
		}

		// Query function to call, and return
		return query(queryObj, orderByStr, offset, limit);
	}
	
	//
	// Aggregation command support
	//

	/**
	 * Performs a query, and aggregate the result accordingly
	 * 
	 * Note : When extending this class, with custom aggregation handlers (like hazelcast), you should only replace this function.
	 *
	 * @param aggregationTerms to aggregate data with
	 * @param queryClause to filter the collection with, can be null
	 * 
	 * @return Aggregation result to the corresponding terms
	 */
	@SuppressWarnings("unchecked")
	default BigDecimal[] aggregate(String[] aggregationTerms, Query queryClause) {
		// Aggregate everything directly, if no query is passed
		if( queryClause == null ) {
			return QueryUtils.aggregate((Collection<Object>)(Object)(this), aggregationTerms);
		}

		// Aggregate with query results
		List<V> queryList = query(queryClause, null, -1, -1);
		return QueryUtils.aggregate((Collection<Object>)(Object)(queryList), aggregationTerms);
	}

	/**
	 * Performs a query, and aggregate the result accordingly
	 * 
	 * @param   aggregationTerms to aggregate data with
	 * @param   where query statement
	 * @param   where clause values array
	 * 
	 * @return Aggregation result to the corresponding terms
	 */
	default BigDecimal[] aggregate(String[] aggregationTerms, String whereClause, Object[] whereValues) {
		// Query object to use
		Query queryObj = null;

		// Where clause to convert to query object
		if( whereClause != null ) {
			queryObj = Query.build(whereClause, whereValues);
		}
		
		// Aggregation with query (where applicable)
		return aggregate(aggregationTerms, queryObj);
	}

	/**
	 * Performs a query, and aggregate the result accordingly
	 * 
	 * @param aggregationTerms to aggregate data with
	 * 
	 * @return Aggregation result to the corresponding terms
	 */
	default BigDecimal[] aggregate(String[] aggregationTerms) {
		return aggregate(aggregationTerms, null);
	}

}