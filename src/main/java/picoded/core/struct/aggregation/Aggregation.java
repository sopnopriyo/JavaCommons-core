package picoded.core.struct.aggregation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Math;
import java.math.BigDecimal;

import picoded.core.conv.GenericConvert;
import picoded.core.struct.MutablePair;
import picoded.core.struct.query.mapreduce.*;

/**
 * Aggregation library to be used with the query library.
 * To extract aggregation metrics across larger dataset.
 *
 * As such filtering of data is similar to the SQL where clause
 *
 * Aggregation to include
 * + count(fieldname)
 * + max(fieldname)
 * + min(fieldname)
 * + avg(fieldname)
 * + sum(fieldname)
 *
 **/
public class Aggregation {
	
	//-------------------
	//
	// MapReduceBase class implementation
	//
	//-------------------

	// Cached memoizer of the MapReduceBase implementation
	protected static Map<String, MapReduceBase> _mapReduceBaseImplementation = null;
	
	/**
	 * MapReduceBase implementation map, used to perform the needed aggregation
	 * 
	 * @return  map, of aggregation function, and MapReduceBase impementation
	 **/
	protected static Map<String, MapReduceBase> mapReduceBaseImplementation() {
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
	
	//-------------------
	//
	// Utility functions
	//
	//-------------------

	/**
	 * Process in an array of aggregation func and field string, and split information up respectively
	 * 
	 * @param aggregationArray  of function and field terms to compute on
	 * 
	 * @return  pair of the function names, and field names to perform aggegation on
	 */
	protected static MutablePair<String[], String[]> extractAggregrationInfo(String[] aggregationArray) {
		// Return the func and field name array
		String[] funcNames  = new String[ aggregationArray.length ];
		String[] fieldNames = new String[ aggregationArray.length ];

		// Iterate each aggregation term
		for( int i = 0; i < aggregationArray.length; ++i ) {
			// Quick cleanup
			String funcNameAndField = aggregationArray[i].trim();

			// Get left and right bracket
			int leftBracket  = funcNameAndField.indexOf('(');
			int rightBracket = funcNameAndField.lastIndexOf(')');

			// Validate the bracket
			if( leftBracket <= 0 || rightBracket != (funcNameAndField.length()-1) ) {
				throw new RuntimeException("Invalid aggregation function/parameter format : "+funcNameAndField);
			}
	
			// Store the aggregation info
			funcNames[i]  = funcNameAndField.substring(0, leftBracket);
			fieldNames[i] = funcNameAndField.substring(leftBracket, rightBracket);
		}

		// Return the full func name / field names split
		return new MutablePair<String[],String[]>(funcNames, fieldNames);
	}

	//-------------------
	//
	// Aggregration constructor
	//
	//-------------------

	/**
	 * Taking in a a set of aggregation terms, and compute them against a collection
	 * 
	 * @param  aggregationArray to aggregate using
	 * @param  dataSet to compute on
	 * 
	 * @return  BigDecimal array of the corresponding aggregation result
	 **/











	/**
	 *
	 * Terms [ "count(*)", "max(investment_total)", "sum(investment_count)" ]
	 * Query
	 */
	public static Map<String, Object> aggregation(String[] inAggregationTermsAndParameters,
		Collection<Map<String, Object>> dataSet) {
		// 1. Ensure input safety
		if (inAggregationTermsAndParameters == null || inAggregationTermsAndParameters.length <= 0) {
			throw new RuntimeException("No aggregation terms given.");
		}
		
		if (dataSet == null) {
			throw new RuntimeException("No dataSet given.");
		}
		
		// 2. Extract the terms and arguments
		String[] aggTerms = extractAggregationTerms(inAggregationTermsAndParameters);
		String[] aggArguments = extractAggregationArguments(inAggregationTermsAndParameters);
		
		if (aggTerms.length <= 0) {
			throw new RuntimeException("Unable to extract aggregation terms.");
		}
		
		if (aggArguments.length <= 0) {
			throw new RuntimeException("Unable to extract aggregation arguments.");
		}
		
		if (aggTerms.length != aggArguments.length) {
			throw new RuntimeException("Aggregation terms and arguments lengths differ.");
		}
		
		// 3. Based on term, run the correct aggregation function
		Map<String, Object> aggResult = new HashMap<String, Object>();
		for (int i = 0; i < aggTerms.length; ++i) {
			if (!defaultAggFunctionDefinitions().containsKey(aggTerms[i])) {
				aggResult.put(inAggregationTermsAndParameters[i],
					"Unable to fetch AggregationFunction for " + inAggregationTermsAndParameters[i]);
			} else {
				aggResult.put(inAggregationTermsAndParameters[i],
					defaultAggFunctionDefinitions().get(aggTerms[i]).apply(aggArguments[i], dataSet));
			}
		}
		
		return aggResult;
	}
	
	public static String[] extractAggregationTerms(String[] aggregationTerms) {
		String[] result = new String[aggregationTerms.length];
		
		for (int i = 0; i < aggregationTerms.length; ++i) {
			result[i] = aggregationTerms[i].substring(0, aggregationTerms[i].indexOf('('));
		}
		
		return result;
	}
	
	public static String[] extractAggregationArguments(String[] aggregationTerms) {
		String[] result = new String[aggregationTerms.length];
		
		for (int i = 0; i < aggregationTerms.length; ++i) {
			result[i] = aggregationTerms[i].substring(aggregationTerms[i].indexOf('(') + 1,
				aggregationTerms[i].lastIndexOf(')'));
		}
		
		return result;
	}
	
	//-------------------
	//
	// Aggregation Implementations
	//
	//-------------------

	public static String COUNT = "count";
	public static String MAX = "max";
	public static String MIN = "min";
	public static String AVG = "avg";
	public static String SUM = "sum";
	
	// this variable is here temporarily to make the avg function not crash 
	// if the result has a repeating non terminating decimal portion
	// will limit the result to ~ 24 decimal places
	//
	// Note : precision vs scale terminology - https://stackoverflow.com/questions/35435691/bigdecimal-precision-and-scale
	public static int AVG_RESULT_MAX_SCALE = 24;
	
	private static Map<String, AggregationFunction> aggFuncDefinitions = null;
	
	public synchronized static Map<String, AggregationFunction> defaultAggFunctionDefinitions() {
		if (aggFuncDefinitions != null) {
			return aggFuncDefinitions;
		}
		
		aggFuncDefinitions = new HashMap<String, AggregationFunction>();
		aggFuncDefinitions.put("count", count);
		aggFuncDefinitions.put("max", max);
		aggFuncDefinitions.put("min", min);
		aggFuncDefinitions.put("avg", avg);
		aggFuncDefinitions.put("sum", sum);
		
		return aggFuncDefinitions;
	}
	
	public static AggregationFunction count = (fieldName, inputDataSet) -> {
		int countResult = 0;
		for (Map<String, Object> mapData : inputDataSet) {
			if (mapData.get(fieldName) == null)
				continue;
			if (mapData.containsKey(fieldName)) {
				++countResult;
			}
		}
		return new BigDecimal(Integer.toString(countResult));
	};
	
	public static AggregationFunction max = (fieldName, inputDataSet) -> {
		BigDecimal maxResult = null;
		for (Map<String, Object> mapData : inputDataSet) {
			if (mapData.get(fieldName) == null)
				continue;
			BigDecimal currentNumber = new BigDecimal(mapData.get(fieldName).toString());
			if (maxResult == null) {
				maxResult = currentNumber;
			}
			
			maxResult = currentNumber.max(maxResult);
		}
		return maxResult;
	};
	
	public static AggregationFunction min = (fieldName, inputDataSet) -> {
		BigDecimal minResult = null;
		for (Map<String, Object> mapData : inputDataSet) {
			if (mapData.get(fieldName) == null)
				continue;
			BigDecimal currentNumber = new BigDecimal(mapData.get(fieldName).toString());
			if (minResult == null) {
				minResult = currentNumber;
			}
			
			minResult = currentNumber.min(minResult);
		}
		return minResult;
	};
	
	public static AggregationFunction avg = (fieldName, inputDataSet) -> {
		BigDecimal avgResult = new BigDecimal(0);
		BigDecimal sumResult = new BigDecimal(0);
		int valueCount = 0;
		
		for (Map<String, Object> mapData : inputDataSet) {
			if (mapData.get(fieldName) == null)
				continue;
			BigDecimal currentNumber = new BigDecimal(mapData.get(fieldName).toString());
			sumResult = sumResult.add(currentNumber);
			++valueCount;
		}
		
		avgResult = sumResult.divide(new BigDecimal(valueCount), AVG_RESULT_MAX_SCALE,
			BigDecimal.ROUND_HALF_UP);
		return avgResult;
	};
	
	public static AggregationFunction sum = (fieldName, inputDataSet) -> {
		BigDecimal sumResult = new BigDecimal(0);
		for (Map<String, Object> mapData : inputDataSet) {
			if (mapData.get(fieldName) == null)
				continue;
			BigDecimal currentNumber = new BigDecimal(mapData.get(fieldName).toString());
			sumResult = sumResult.add(currentNumber);
		}
		
		return sumResult;
	};
}