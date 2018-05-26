package picoded.core.conv;

import java.util.*;

/**
 * Utility class and function, which handles the extraction of nested objects, 
 * in a Map/List using single key strings.
 * 
 * In a flatten FQDN setup, this would be a direct get operation
 * 
 * However in a complex nested object structure, this will
 * attempt every possible interpration of the string path,
 * and return the first possible result path.
 * 
 * For example
 * 
 * ```
 * Object base = ConvertJSON.toMap("{ \"a\" : { \"b.c\" : [1,2], \"0\":\"haha\" } }");
 * assertEquals(1, NestedObjectFetch.fetchObject(base, "a.b.c[0]"));
 * assertEquals(2, NestedObjectFetch.fetchObject(base, "a[b.c][1]"));
 * assertEquals("haha", NestedObjectFetch.fetchObject(base, "a.0"));
 * assertEquals("haha", NestedObjectFetch.fetchObject(base, "a[0]"));
 * ```
 **/
public class NestedObjectFetch {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected NestedObjectFetch() {
		throw new IllegalAccessError("Utility class");
	}
	
	//--------------------------------------------------------------------------------------------------
	//
	// nested object fetching
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Gets an object from a map / list,
	 * That could very well be, a map inside a list, inside a map, inside a .....
	 * 
	 * This is intended to easily fetch nested values inside an object / list
	 * in a fashion that is similar to Javascript dot notation
	 * 
	 * For example the following objects
	 * ```
	 * // PS : I know this is a JS example in java comments, =/
	 * var base = {
	 * 	"a" : {
	 * 		"b.c" : [
	 * 			"hello",
	 * 			"world"
	 * 		],
	 * 		"0" : "haha"
	 * 	}
	 * }
	 * ```
	 * 
	 * Will give the following result when used
	 * 
	 * `fetchObject(base, "a.b.c[0]")` gives `hello`
	 * `fetchObject(base, "a["b.c"][0]")` gives `hello`
	 * `fetchObject(base, "a.0")` gives `haha`
	 * `fetchObject(base, "a[0]")` gives `haha`
	 * 
	 * Note that this returns immediately on the first match, and makes no attempt
	 * at resolving any conflicts nor duplicate values
	 * 
	 * Note that at each iteration step, it attempts to do a FULL key match first,
	 * before the next iteration depth, in various combinations, as such in event
	 * of multiple possilbe interpration, it give prefence for full path matches.
	 * 
	 * The following matches are done in sequence, till a valid response is given
	 * 
	 * - `this.is.1.annoying.deep.search`
	 * - `this.is.1.annoying.deep`, `search`
	 * - `this.is.1.annoying`, `deep.search`
	 * - `this.is.1.annoying`, `deep`, `search`
	 * - `this.is.1`, `annoying.deep.search`
	 * - `this.is.1.annoying`, `annoying.deep`, `search`
	 * - `this.is.1.annoying`, `annoying`, `deep.search`
	 * - `this.is.1.annoying`, `annoying`, `deep`, `search`
	 *
	 * @param base        Map / List to manipulate from
	 * @param objectPath  The input key to fetch, possibly nested
	 * @param fallback    The fallback default (if not convertable)
	 * @return  The fetched object, always possible unless fallbck null
	 */
	public static Object fetchObject(Object base, String objectPath, Object fallback) {
		
		//-----------------------------------------------------------------------
		//
		//  Quick sanity checks, and full path matching
		//
		//-----------------------------------------------------------------------

		// Invalid base -> null, 
		// or not ( map OR list ) -> fallback
		//
		// Enters fallback, as its pretty much impossible to get any "nested" object
		if (base == null || !((base instanceof Map) || (base instanceof List))) {
			return fallback;
		}
		
		// Return object variable is initialized for reuse
		Object ret = null;

		// Full objectPath fetching found -> if found it is returned =)
		//
		// For example the following base `{ "a.b.c" : "hello" }` matches
		// against `a.b.c` but not `a.b`
		ret = MapOrListUtil.getValue(base, objectPath, null);
		if (ret != null) {
			return ret;
		}
		
		// Fallsback if objectPath is ALREADY EMPTY !
		// cause nothing is found, and nothing else can be done
		// if the full objectPath fetching fail. 
		//
		// This is intentionally done after the full 
		// MapOrListUtil.getValue
		//
		// For example if the base was `{ "" : "hello" }`
		// the above getValue would have "returned"
		//
		// Hence at this point fallback occurs
		if (objectPath == null || objectPath.length() <= 0) {
			return fallback;
		}
		
		//-----------------------------------------------------------------------
		//
		//  Split path matching
		//
		//-----------------------------------------------------------------------

		// Get the split pathing of the objectPath
		String[] splitPath = splitObjectPath(objectPath);
		int splitPathLength = splitPath.length;

		//
		// For each possible interpration of the split path, 
		// attempt to fetch the target object, using the longest path first
		//
		// For the following search `this.is.1.annoying.deep.search`
		//
		// The following possible matches are done in priority till a result is found.
		//
		// `this.is.1.annoying.deep.search`
		// `this.is.1.annoying.deep`, `search`
		// `this.is.1.annoying`, `deep.search`
		// `this.is.1.annoying`, `deep`, `search`
		// `this.is.1`, `annoying.deep.search`
		// `this.is.1.annoying`, `annoying.deep`, `search`
		// `this.is.1.annoying`, `annoying`, `deep.search`
		// `this.is.1.annoying`, `annoying`, `deep`, `search`
		//
		for(int idx=splitPathLength; idx >=0; --idx) {

			// The prefix text to use, to get the nested map/list
			// to attempt to get the subsequent objects
			String[] prefixArray = ArrayConv.subarray(splitPath, 0, idx);
			String prefixStr = String.join(".", prefixArray);

			// The nested base to use, if found
			Object nestedBase = MapOrListUtil.getValue(base, prefixStr);
			if( nestedBase == null ) {
				// No nested base found, skips
				continue;
			}

			// The suffix text, to try to extract from the nested base object
			String[] suffixArray = ArrayConv.subarray(splitPath, idx, splitPathLength);
			String suffixStr = String.join(".", suffixArray);

			// Recursively attempts to fetch a suffix nested object, till its found
			ret = fetchObject(nestedBase, suffixStr, null);

			// Return if result was found
			if( ret != null ) {
				return ret;
			}
		}

		//-----------------------------------------------------------------------
		//
		//  Common mistakes workarounds 
		//
		//-----------------------------------------------------------------------
		
		// Try again with trimmed object path
		// ` why ` -> `why`
		String objectPathTrim = objectPath.trim();
		if(!objectPath.equals(objectPathTrim)) {
			return fetchObject(base, objectPathTrim, fallback);
		}

		// Trim off usesless starting ".dots", and try again
		// `.doh` -> `doh`
		if(objectPath.startsWith(".")) {
			return fetchObject(base, objectPath.substring(1), fallback);
		}

		// Trim off usesless wrapping brackets
		// `[wrap][it]` -> `wrap[it]`
		if(objectPath.startsWith("[")) {
			int closingBracket = objectPath.indexOf("]",1);
			String unwrappedPath = objectPath.substring(1,closingBracket)+objectPath.substring(closingBracket+1);
			return fetchObject(base, unwrappedPath, fallback);
		}

		//-----------------------------------------------------------------------
		//
		//  Final fallback
		//
		//-----------------------------------------------------------------------

		// All else failed, including full key fetch -> fallback
		return fallback;
	}
	
	/**
	 * Null fallback alternative for fetchObject
	 *
	 * @param base        Map / List to manipulate from
	 * @param objectPath  The input key to fetch, possibly nested
	 * 
	 * @return  The fetched object, always possible unless fallbck null
	 */
	public static Object fetchObject(Object base, String objectPath) {
		return fetchObject(base, objectPath, null);
	}

	//--------------------------------------------------------------------------------------------------
	//
	// Key name splitting, used inside NestedObjectFetch
	//
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Split the object string path into their respective component
	 * (Utility function used inside NestedObjectFetch)
	 * 
	 * For example `enter[into].the.breach[0]` becomes a string list of `["enter", "into", "the", "breach", "0"]`
	 *
	 * @param key      The object path to split
	 *
	 * @return         The split object path, possibly empty array if key is invalid?
	 **/
	public static String[] splitObjectPath(String key) {
		return GenericConvert.toStringArray(splitObjectPath(key, null));
	}
	
	/**
	 * Split the object string path into their respective component
	 * (Utility function used inside NestedObjectFetch)
	 * 
	 * For example `enter[into].the.breach[0]` becomes a string list of `["enter", "into", "the", "breach", "0"]`
	 *
	 * @param key       The object path to split
	 * @param ret       [Optional] return list, to append the key result into
	 *
	 * @return          The split object path, possibly empty array if key is invalid?
	 **/
	protected static List<String> splitObjectPath(String key, List<String> ret) {
		// Return array list of string,
		// initialize if null
		if (ret == null) {
			ret = new ArrayList<String>();
		}
		
		//
		// No more key parts, terminates
		//
		// This is the actual termination point for the recursive function
		//
		if (key == null || key.length() <= 0) {
			//if (ret.size() < 0) {
			//ret.add("");
			//}
			return ret;
		}
		
		// Trim off useless spaces, and try again (if applicable)
		int keyLen = key.length();
		key = key.trim();
		if (key.length() != keyLen) {
			return splitObjectPath(key, ret);
		}
		
		// Trim off useless starting ".dots" and try again
		if (key.startsWith(".")) {
			return splitObjectPath(key.substring(1), ret);
		}
		
		// Fetches the next 2 index points (most probably seperator of token parts)
		int dotIndex = key.indexOf('.');
		int leftBracketIndex = key.indexOf('[');
		
		// No match found, assume last key
		if (dotIndex < 0 && leftBracketIndex < 0) {
			ret.add(key);
			return ret;
		}
		
		// Left and right string parts to recursively process
		String leftPart;
		String rightPart;

		// Begins left/right part splitting and processing
		if (leftBracketIndex == 0) {
			//
			// Array bracket fetching start
			// This is most likely an array fetching,
			// but could also be a case of map fetching with string
			//
			int rightBracketIndex = key.indexOf(']', 1);
			if (rightBracketIndex <= 0) {
				throw new RuntimeException("Missing closing ']' right bracket for key : " + key);
			}
			
			//
			// Get the left part within the bracket, and the right part after it
			//
			// Format: [leftPart]rightPart___
			//
			leftPart = key.substring(1, rightBracketIndex).trim();
			rightPart = key.substring(rightBracketIndex + 1).trim();
			
			// Sanatize the left path from quotation marks
			if (leftPart.length() > 1
				&& ((leftPart.startsWith("\"") && leftPart.endsWith("\"")) || (leftPart
					.startsWith("\'") && leftPart.endsWith("\'")))) {
				leftPart = leftPart.substring(1, leftPart.length() - 1);
			}
			
		} else if (dotIndex >= 0 && (leftBracketIndex < 0 || dotIndex <= leftBracketIndex)) {
			//
			// Dot Index exists, and is before left bracket index OR there is no left bracket index
			//
			// This is most likely a nested object fetch -> so recursion is done
			//
			
			//
			// Get the left part before the dot, and right part after it
			//
			// Format: leftPart.rightPart___
			//
			leftPart = key.substring(0, dotIndex); //left
			rightPart = key.substring(dotIndex + 1); //right
			
		} else if (leftBracketIndex > 0) {
			//
			// Left bracket index exists, and there is no dot before it
			//
			// This is most likely a nested object fetch -> so recursion is done
			//
			
			//
			// Get the left part before the left bracket, and right part INCLUDING the left bracket it
			//
			// Format: leftPart[rightPart___
			//
			leftPart = key.substring(0, leftBracketIndex); //left
			rightPart = key.substring(leftBracketIndex); //[right]
			
		} else {
			throw new RuntimeException("Unexpected key format : " + key);
		}
		
		// Add left key to return set
		ret.add(leftPart);
		
		// There is no right key, ends and terminate at recusive termination point above
		if (rightPart == null || rightPart.length() <= 0) {
			return splitObjectPath(null, ret);
		}
		
		// ELSE : recursively process the right keys
		return splitObjectPath(rightPart, ret);
	}
	
}