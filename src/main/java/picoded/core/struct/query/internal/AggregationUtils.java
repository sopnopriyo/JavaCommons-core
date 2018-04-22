package picoded.core.struct.query.internal;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import picoded.core.conv.GenericConvert;
import picoded.core.struct.MutablePair;
import picoded.core.struct.query.mapreduce.*;

/**
 * Collection of utility functions used by the query.Aggregation class
 */
public class AggregationUtils {

	//---------------------------------------
	//
	// MapReduceBase refrencing
	//
	//---------------------------------------

	// Cached memoizer of the MapReduceBase implementation
	protected static Map<String, MapReduceBase> _mapReduceBaseImplementation = null;
	
	/**
	 * MapReduceBase implementation map, used to perform the needed aggregation
	 * 
	 * @return  map, of aggregation function, and MapReduceBase impementation
	 **/
	public static Map<String, MapReduceBase> mapReduceBaseImplementation() {
		if (_mapReduceBaseImplementation != null) {
			return _mapReduceBaseImplementation;
		}
		
		// Initialize the mapreduce classes
		Map<String, MapReduceBase> map = new ConcurrentHashMap<String, MapReduceBase>();
		map.put("count", new Count());
		map.put("max",   new Max());
		map.put("min",   new Min());
		map.put("sum",   new Sum());
		map.put("avg",   new Avg());
		
		// Save the implementation map for reuse
		_mapReduceBaseImplementation = map;
		return _mapReduceBaseImplementation;
	}
	
	//---------------------------------------
	//
	// Utility functions
	//
	//---------------------------------------

	/**
	 * Process in an array of aggregation func and field string, and split information up respectively
	 * 
	 * @param aggregationArray  of function and field terms to compute on
	 * 
	 * @return  pair of the function names, and field names to perform aggegation on
	 */
	public static MutablePair<String[], String[]> extractAggregrationInfo(String[] aggregationArray) {
		// 1. prepare the Return func and field name array
		String[] funcNames  = new String[ aggregationArray.length ];
		String[] fieldNames = new String[ aggregationArray.length ]; 

		// 2. Iterate each aggregation term
		for( int i = 0; i < aggregationArray.length; ++i ) {
			// 3. Quick variable safety
			String funcNameAndField = aggregationArray[i].trim();

			// 3. Get left and right bracket of a `function(field)`
			int leftBracket  = funcNameAndField.indexOf('(');
			int rightBracket = funcNameAndField.lastIndexOf(')');

			// 4. Validate the bracket values
			if( leftBracket <= 0 || rightBracket != (funcNameAndField.length()-1) ) {
				throw new RuntimeException("Invalid aggregation function/parameter format : "+funcNameAndField);
			}
	
			// 4. Store the aggregation info
			funcNames[i]  = funcNameAndField.substring(0, leftBracket);
			fieldNames[i] = funcNameAndField.substring(leftBracket+1, rightBracket);
		}

		// 5. Return the full func name / field names split
		return new MutablePair<String[],String[]>(funcNames, fieldNames);
	}

	/**
	 * Given a function name array, return an array of the respective MapReduceBase implementation
	 * 
	 * @param  implmentationMap   mapReduceBase implmentations to use
	 * @param  funcNames          array of function names to use
	 * 
	 * @return  MapReduceBase[] implmentation of function names
	 */
	public static MapReduceBase[] prepareMapReduceBaseArray( 
		Map<String, MapReduceBase> implmentationMap, String[] funcNames 
	) {
		// 1. get expected result length, and initialize the result array
		int len = funcNames.length;
		MapReduceBase[] res = new MapReduceBase[ len ];

		// 2. initialize each function respective MapReduceBase
		for(int i=0; i<len; ++i) {

			// 3. validate that the function has a valid MapReduceBase implmentation
			MapReduceBase base = implmentationMap.get( funcNames[i] );
			if( base == null ) {
				throw new RuntimeException("Unable to fetch Aggregation Function (MapReduceBase) for "+funcNames[i]);
			}

			// 4. Initialize new instance of the MapReduceBase implmentation
			res[i] = base.newInstance();
		}

		// 4. Return the initialized array
		return res;
	}

	/**
	 * Given a dataset collection, and the MapReduceBase array,
	 * Compute and return the BigDecimal result
	 * 
	 * @param  mapreduceArray to use and perform computaton with
	 * @param  fieldNames     fieldNames to the respective mapreduceArray, to extract values from
	 * @param  dataSet        dataset to extract values from to compute
	 * 
	 * @return  BigDecimal array of results
	 */
	public static BigDecimal[] computeMapReduceBase(
		MapReduceBase[] mapreduceArray,
		String[] fieldNames,
		Collection<Object> dataSet
	) {
		// 1. Get the expected result length, and initialize the result array
		int len = mapreduceArray.length;
		BigDecimal[] ret = new BigDecimal[len];

		// 2. Iterate the dataset, and the MapReduceBase
		for (Object mapDataObj : dataSet) {
			Map<String,Object> mapData = GenericConvert.toStringMap(mapDataObj);
			for(int i=0; i<len; ++i) {
				mapreduceArray[i].map(mapData, fieldNames[i]);
			}
		}

		// 3. reduce out the result for each mapreduce implmentation
		//    into the resulting array
		for(int i=0; i<len; ++i) {
			ret[i] = mapreduceArray[i].reduce();
		}

		// 4. return the array of BigDecimal's
		return ret;
	}
}