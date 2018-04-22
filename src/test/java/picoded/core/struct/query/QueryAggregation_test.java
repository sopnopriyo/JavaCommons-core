package picoded.core.struct.aggregation;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import picoded.core.struct.query.Query;

import java.math.BigDecimal;

public class QueryAggregation_test {
	
	//------------------------------------------
	//
	// Test setup
	//
	//------------------------------------------
	
	// Data set to query against
	private List<Map<String, Object>> fullDataSet = null;

	// Single "row" record to generate
	private Map<String, Object> createSingleMap(
		String name,
		int intVal, double doubleVal, String stringVal
	) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("name", name);
		ret.put("intVal", intVal);
		ret.put("doubleVal", doubleVal);
		ret.put("stringVal", stringVal);
		return ret;
	}
	
	// Initializing full dataset
	private void initialiseFullDataSet() {
		// Skip if already initialized
		if( fullDataSet != null ) {
			return;
		}
		// Initialize dataset
		fullDataSet = new ArrayList<Map<String, Object>>();
		fullDataSet.add(createSingleMap("bob", 0, 0.0, "0"));
		fullDataSet.add(createSingleMap("bob",1, 1.1, "1"));
		fullDataSet.add(createSingleMap("bob",2, 2.2, "2"));
		fullDataSet.add(createSingleMap("bob",3, 3.3, "3"));
		fullDataSet.add(createSingleMap("bob",4, 4.4, "4"));
		fullDataSet.add(createSingleMap("tom",5, 5.5, "5"));
		fullDataSet.add(createSingleMap("tom",6, 6.6, "6"));
		fullDataSet.add(createSingleMap("tom",7, 7.7, "7"));
		fullDataSet.add(createSingleMap("tom",8, 8.8, "8"));
		fullDataSet.add(createSingleMap("tom",9, 9.9, "9"));
	}

	// Reusable queries
	Query queryAll = Query.build("name != ?", new Object[] { "any-value" });
	Query queryBob = Query.build("name = ?", new Object[] { "bob" });
	Query queryTom = Query.build("name = ?", new Object[] { "tom" });

	// Initialize full data set with reusable query
	@Before
	public void setup() {
		initialiseFullDataSet();
	}
	
	//------------------------------------------
	//
	// Sanity test
	//
	//------------------------------------------
	
	@Test
	public void sanity() {
		assertNotNull(fullDataSet);
	}
	
	//------------------------------------------
	//
	// COUNT
	//
	//------------------------------------------

	@Test
	public void testCountAll() {
		// Query sanity check
		assertEquals(10, queryAll.search(fullDataSet).size());

		// Aggregation counting
		BigDecimal res = queryAll.singleAggregation(fullDataSet, "count(*)");
		assertNotNull(res);
		assertEquals(10, res.intValue());
	}

	@Test
	public void testCountIntVal() {
		// Aggregation counting
		BigDecimal res = queryAll.singleAggregation(fullDataSet, "count(intVal)");
		assertNotNull(res);
		assertEquals(10, res.intValue());
	}
	
	@Test
	public void testCountDoubleVal() {
		// Aggregation counting
		BigDecimal res = queryBob.singleAggregation(fullDataSet, "count(doubleVal)");
		assertNotNull(res);
		assertEquals(5, res.intValue());
	}
	
	@Test
	public void testCountStringVal() {
		// Aggregation counting
		BigDecimal res = queryTom.singleAggregation(fullDataSet, "count(stringVal)");
		assertNotNull(res);
		assertEquals(5, res.intValue());
	}
	
	//------------------------------------------
	//
	// MIN
	//
	//------------------------------------------
	@Test
	public void testMinIntVal() {
		BigDecimal res = queryTom.singleAggregation(fullDataSet, "min(intVal)");
		assertNotNull(res);
		assertEquals(5, res.intValue());
	}
	
	@Test
	public void testMinDoubleVal() {
		BigDecimal res = queryTom.singleAggregation(fullDataSet, "min(doubleVal)");
		assertNotNull(res);
		assertEquals(5.5, res.doubleValue(), 0);
	}
	
	@Test
	public void testMinStringVal() {
		BigDecimal res = queryBob.singleAggregation(fullDataSet, "min(stringVal)");
		assertNotNull(res);
		assertEquals(0.0, res.doubleValue(), 0);
	}
	

	/*
	//------------------------------------------
	//
	// MULTIPLE
	//
	//------------------------------------------
	@Test
	public void testMultiple() {
		String countIntVal = Aggregation.COUNT + "(intVal)";
		String maxIntVal = Aggregation.MAX + "(intVal)";
		String minDoubleVal = Aggregation.MIN + "(doubleVal)";
		String sumDoubleVal = Aggregation.SUM + "(doubleVal)";
		String avgStringVal = Aggregation.AVG + "(stringVal)";
		
		String[] aggTermsAndArgs = new String[] { countIntVal, maxIntVal, minDoubleVal, sumDoubleVal,
			avgStringVal };
		Map<String, Object> res = Aggregation.aggregation(aggTermsAndArgs, fullDataSet);
		
		assertEquals(10, ((BigDecimal) res.get(countIntVal)).intValue());
		assertEquals(9, ((BigDecimal) res.get(maxIntVal)).intValue());
		assertEquals(0.0, ((BigDecimal) res.get(minDoubleVal)).doubleValue(), 0);
		assertEquals(49.5, ((BigDecimal) res.get(sumDoubleVal)).doubleValue(), 0);
		assertEquals(4.5, ((BigDecimal) res.get(avgStringVal)).doubleValue(), 0);
	}
	
	//------------------------------------------
	//
	// MAX
	//
	//------------------------------------------
	@Test
	public void testMaxIntVal() {
		String aggTermAndArg = Aggregation.MAX + "(intVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(9, val.intValue());
		assertEquals(9.0, val.doubleValue(), 0);
	}
	
	@Test
	public void testMaxDoubleVal() {
		String aggTermAndArg = Aggregation.MAX + "(doubleVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(9.9, val.doubleValue(), 0);
	}
	
	@Test
	public void testMaxStringVal() {
		String aggTermAndArg = Aggregation.MAX + "(stringVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(9, val.intValue());
		assertEquals(9.0, val.doubleValue(), 0);
	}
	
	//------------------------------------------
	//
	// AVG
	//
	//------------------------------------------
	@Test
	public void testAvgInt() {
		String aggTermAndArg = Aggregation.AVG + "(intVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(4.5, val.doubleValue(), 0);
	}
	
	@Test
	public void testAvgDouble() {
		String aggTermAndArg = Aggregation.AVG + "(doubleVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(4.95, val.doubleValue(), 0);
	}
	
	@Test
	public void testAvgString() {
		String aggTermAndArg = Aggregation.AVG + "(stringVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(4.5, val.doubleValue(), 0);
	}
	
	//------------------------------------------
	//
	// SUM
	//
	//------------------------------------------
	@Test
	public void testSumInt() {
		String aggTermAndArg = Aggregation.SUM + "(intVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(45, val.intValue());
	}
	
	@Test
	public void testSumDouble() {
		String aggTermAndArg = Aggregation.SUM + "(doubleVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(49.5, val.doubleValue(), 0);
	}
	
	@Test
	public void testSumString() {
		String aggTermAndArg = Aggregation.SUM + "(stringVal)";
		Map<String, Object> res = Aggregation
			.aggregation(new String[] { aggTermAndArg }, fullDataSet);
		
		BigDecimal val = (BigDecimal) res.get(aggTermAndArg);
		assertEquals(45, val.intValue());
		assertEquals(45.0, val.doubleValue(), 0);
	}
	*/
}