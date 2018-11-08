package picoded.core.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import picoded.core.struct.query.Query;
import picoded.core.struct.query.QueryUtils;

/**
 * Query interface for a collection of maps
 */
public interface QueryCollectionMap<V extends Map> extends Collection<V> {

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
	 * @return  The DataObject[] array
	 **/
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
	 * @return  The DataObject[] array
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
	
}