package picoded.core.struct.query;

import java.math.BigDecimal;
import java.util.*;

/**
 * Collection of query utility functions, used to query a collection and sort its results
 * 
 * As per everything else in the query library. This is meant to search and sort
 * collections of Map<String,Object> type objects
 */
public class QueryUtils {
	
	/**
	 * Takes a list of values, and extract its offset of values.
	 * 
	 * This can be considered a "silent" version of subList,
	 * where out of bound results would simply return a blank list.
	 * 
	 * @param inList  list to get get subList from. Null returns a blank list
	 * @param offset  result to return from, use -1 to ignore
	 * @param limit   number of results to return at max, use -1 to ignore
	 * 
	 * @return  sublist of the result chosen (if any)
	 */
	public static <V> List<V> offsetList(List<V> inList, int offset, int limit) {
		// Parameter checks
		if (inList == null) {
			return new ArrayList<V>();
		} 

		// Get size of raw list
		int size = inList.size();

		// Return a blank list (there is nothing to offset)
		if( size == 0 ) {
			return inList;
		}
		
		// Get sublist if needed
		if (offset >= 1 || limit >= 1) {
			// Out of bound, return blank
			if (offset >= size) {
				return new ArrayList<V>();
			}
			
			// Get the ending index (based on limit)
			int end = size;
			if (limit > -1) {
				end = offset + limit;
			}
			
			// Ensures the upper end does not go out of bound
			if (end > size) {
				end = size;
			}

			// Return blank list if equals sizing
			if (offset == end) {
				return new ArrayList<V>();
			}
			
			// Get and return sublist
			return inList.subList(offset, end);
		}
		
		// no offset occur, return the list itself
		return inList;
	}
	
	/**
	 * Takes a list and sort by the orderBy string
	 * 
	 * @param  inList      list to sort, and returns it sorted. Returns null if null.
	 * @param  orderByStr  order by string to apply
	 * 
	 * @return  inList sorted if modifiable, else a new list sorted
	 */
	public static <V> List<V> sortList(List<V> inList, String orderByStr) {
		// Return null, if inlist is null
		if( inList == null ) {
			return null;
		}

		// Sorting the order, if needed
		if ( orderByStr != null && (orderByStr = orderByStr.trim()).length() > 0) {
			// Creates the order by sorting, with _oid
			OrderBy<V> sorter = new OrderBy<V>(orderByStr);
			
			// // Let recast the list if it
			// // might not be modifiable (so that we can modify it)
			// // @NOTE : To benchmark and consider if this check 
			// //         is worth the performance improvements?
			// if(!(inList instanceof ArrayList)) {
			// 	inList = new ArrayList<V>(inList);
			// }

			// Lets try to sort the list
			try {
				Collections.sort(inList, sorter);
			} catch(UnsupportedOperationException e) {
				// Ok sorting failed, lets try again as array list
				// As it might have been read only
				inList = new ArrayList<V>(inList);
				Collections.sort(inList, sorter);
			}
		}
		
		// Either : Nothing changed, lets keep it
		// or the list got sorted, and lets return it
		return inList;
	}

	/**
	 * Sort and limit the result of a list
	 *
	 * @param   list of DataObject to sort and return. Null returns a blank list
	 * @param   query string to sort the order by, use null to ignore
	 * @param   offset of the result to display, use -1 to ignore
	 * @param   number of objects to return max
	 *
	 * @return  list to return
	 **/
	public static <V> List<V> sortAndOffsetList(List<V> list, String orderByStr, int offset, int limit) {
		List<V> sortedList = sortList(list, orderByStr);
		return offsetList(sortedList, offset, limit);
	}

	/**
	 * Utility funciton, used to sort and limit the result of a list
	 *
	 * @param   collection for aggregation
	 * @param   aggregationTerms for aggregation
	 *
	 * @return  big decimal array which represents the aggregation terms
	 **/
	public static BigDecimal[] aggregate(Collection<Object> collection, String[] aggregationTerms) {
		Aggregation aggregator = new Aggregation(aggregationTerms);
		return aggregator.compute(collection);
	}

}