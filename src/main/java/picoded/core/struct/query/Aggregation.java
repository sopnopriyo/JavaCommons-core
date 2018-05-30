package picoded.core.struct.query;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import picoded.core.struct.MutablePair;
import picoded.core.struct.query.mapreduce.*;
import picoded.core.struct.query.internal.AggregationUtils;

/**
 * Aggregation library to be used with the query library.
 * To extract aggregation metrics across larger dataset.
 *
 * As such filtering of data is similar to the SQL queries
 *
 * Aggregation to include
 * 
 * + count(fieldname)
 * + max(fieldname)
 * + min(fieldname)
 * + avg(fieldname)
 * + sum(fieldname)
 *
 **/
public class Aggregation {

	//--------------------------------------------------------------------
	// Protected constructor, for the aggregation class
	//--------------------------------------------------------------------
	
	// The original aggregation terms
	protected String[] termsArray = null;

	// Aggregation function to use
	protected String[] funcNames = null;

	// Field names to extract values and compute aggregation on
	protected String[] fieldNames = null;

	// Aggregation fuction mapping
	protected Map<String, MapReduceBase> mapReduceBaseImplementation = null;

	/**
	 * Protected internal constructor
	 * 
	 * @param  aggregationTerms to aggregate using
	 */
	protected Aggregation(String[] aggregationTerms) {
		// 1. Ensure input safety
		if (aggregationTerms == null || aggregationTerms.length <= 0) {
			throw new RuntimeException("No aggregation terms given.");
		}
		
		// 2. Get the mapreduceBase implementation
		mapReduceBaseImplementation = new HashMap<>(AggregationUtils.mapReduceBaseImplementation());

		// 3. Parse the aggregation terms
		MutablePair<String[], String[]> info = AggregationUtils.extractAggregrationInfo(aggregationTerms);

		// 4. Save the terms, funcnames, and fields
		termsArray = aggregationTerms.clone();
		funcNames  = info.getLeft();
		fieldNames = info.getRight();
	}

	//--------------------------------------------------------------------
	// Static Aggregation builder
	//
	// Note that this is intentionally done to keep things consistent
	// with the query implementation, and possible future mapreduce API
	//--------------------------------------------------------------------
	
	/**
	 * Build the aggregator
	 * 
	 * @param  aggregationTerms to aggregate using
	 **/
	public static Aggregation build(String[] queryString) {
		return new Aggregation(queryString);
	}
	
	//--------------------------------------------------------------------
	// Aggregation computation
	//--------------------------------------------------------------------
	
	/**
	 * Compute and get the aggregation result from the collection
	 * 
	 * @param  dataSet to compute on
	 * 
	 * @return  BigDecimal array of the corresponding aggregation result
	 */
	public BigDecimal[] compute( Collection<Object> dataSet ) {
		// 1. Ensure input safety
		if (dataSet == null) {
			throw new RuntimeException("No dataSet given.");
		}
		
		// 2. Initialize MapReduceBase array implmentations
		MapReduceBase[] mapreduceArray = AggregationUtils.prepareMapReduceBaseArray(
			mapReduceBaseImplementation, funcNames
		);

		// 3. Compute the mapreduceArray and return the result
		return AggregationUtils.computeMapReduceBase(
			mapreduceArray,
			fieldNames,
			dataSet
		);
	}

}