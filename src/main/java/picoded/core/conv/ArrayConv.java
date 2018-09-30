package picoded.core.conv;

import org.apache.commons.lang3.ArrayUtils;

import picoded.core.exception.ExceptionMessage;

/**
 *
 * Utility class to help slice out arrays out of arrays without cloning them in memory.
 *
 * This class extends `org.apache.commons.lang3.ArrayUtils`
 *
 * https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/ArrayUtils.html
 *
 **/
public class ArrayConv extends ArrayUtils {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected ArrayConv() {
		throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	}
	
	/**
	 * Utility function to clone an object as a primitive array if possible, else return null.
	 * This is useful in particular when the actual array type is not known at compile time.
	 * 
	 * This only supports, int[], long[], short[], float[], double[], byte[], char[]
	 * 
	 * @param  input value to clone from as a primitive array
	 * 
	 * @return  cloned primitive array, else null
	 */
	static public Object clonePrimitiveArray(Object in) {
		
		// Null clones to null
		if (in == null) {
			return null;
		}
		
		// Array cloning
		if (in.getClass().isArray()) {
			// Is int array
			if (in instanceof int[]) {
				return ArrayConv.clone((int[]) in);
			}
			
			// is long array
			if (in instanceof long[]) {
				return ArrayConv.clone((long[]) in);
			}
			
			// is short array
			if (in instanceof short[]) {
				return ArrayConv.clone((short[]) in);
			}
			
			// is float array
			if (in instanceof float[]) {
				return ArrayConv.clone((float[]) in);
			}
			
			// is double array
			if (in instanceof double[]) {
				return ArrayConv.clone((double[]) in);
			}
			
			// Is byte array
			if (in instanceof byte[]) {
				return ArrayConv.clone((byte[]) in);
			}
			
			// Is char array
			if (in instanceof char[]) {
				return ArrayConv.clone((char[]) in);
			}
		}
		
		// All failed, null
		return null;
	}
	
	/**
	 * This is a helper method to check strings inside array, ignoring
	 * case sensitivity. It supports string values only.
	 * 
	 * @param array        String array containing the values
	 * @param valueToCheck the value to be compared
	 * 
	 * @return true if found, false otherwise
	 */
	static public boolean containsIgnoreCase(String[] array, String valueToCheck) {
		for (String value : array) {
			if (value.equalsIgnoreCase(valueToCheck)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * NOTE: All these were made obselete by apache.commons.ArrayUtils.subarray()
	 **/
	
	// /// Extract out array from starting position onwards
	// public static Object[] sliceObjects(Object[] inArr, int startPos) {
	// 	return sliceObjects(inArr, startPos, inArr.length);
	// }
	//
	// /// Extract out array from starting position to ending position
	// public static Object[] sliceObjects(Object[] inArr, int startPos, int endPos) {
	// 	return Arrays.asList(inArr).subList(startPos, endPos).toArray();
	// }
	//
	// /// Extract out array from starting position onwards
	// public static String[] sliceStrings(String[] inArr, int startPos) {
	// 	return sliceStrings(inArr, startPos, inArr.length);
	// }
	//
	// /// Extract out array from starting position to ending position
	// public static String[] sliceStrings(String[] inArr, int startPos, int endPos) {
	// 	//return (String[])(Object[])(Arrays.asList(inArr).subList(startPos, endPos).toArray());
	// 	return subarray(inArr, startPos, endPos);
	// }
	
}
