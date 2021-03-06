package picoded.core.conv;

import java.util.*;

import picoded.core.exception.ExceptionMessage;

/**
 * Proxy to apache.commons.lang3.StringEscapeUtils. See its full documentation
 * <a href="http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/+commons/lang3/StringEscapeUtils.html">here</a>
 *
 * ### Example
 * ...................................................................javaS
 *
 * Notable static functions inherited (all with single string input)
 * + escapeCsv
 * + escapeEmacScript
 * + escapeHtml4
 * + escapeHtml3
 * + escapeJava
 * + escapeCsv
 *
 * And its inverse function inherited (all with single string input)
 * + unescapedCsv
 * + unescapedEmacScript
 * + unescapedHtml4
 * + unescapedHtml3
 * + unescapedJava
 * + unescapedCsv
 *
 * ----------------------------------------------------------------------------------
 *
 **/
public class StringEscape extends org.apache.commons.text.StringEscapeUtils {
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected StringEscape() {
		throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	}
	
	/**
	 * Direct proxy to escapeHtml4
	 **/
	public static String escapeHtml(String input) {
		return org.apache.commons.text.StringEscapeUtils.escapeHtml4(input);
	}
	
	/**
	 * Direct proxy to unescapeHtml4
	 **/
	public static String unescapeHtml(String input) {
		return org.apache.commons.text.StringEscapeUtils.unescapeHtml4(input);
	}
	
	/**
	 * simple uri append escape function, used for uriEncoding.
	 * @author Daniel Murphy
	 * @see http://web.archive.org/web/20130115153639/http://www.dmurph.com/2011/01/java-uri-encoder/
	 **/
	private static void appendEscaped(StringBuilder uri, char c) {
		if (c <= (char) 0xF) {
			uri.append("%");
			uri.append('0');
			uri.append(HEX[c]);
		} else if (c <= (char) 0xFF) {
			uri.append("%");
			uri.append(HEX[c >> 4]);
			uri.append(HEX[c & 0xF]);
		} else {
			/// unicode
			uri.append('\\');
			uri.append('u');
			uri.append(HEX[c >> 24]);
			uri.append(HEX[(c >> 16) & 0xF]);
			uri.append(HEX[(c >> 8) & 0xF]);
			uri.append(HEX[c & 0xF]);
		}
	}
	
	private static final String MARK = "-_.!~*'()\"";
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();
	
	/**
	 * Simple uri encoder, made from the spec at http://www.ietf.org/rfc/rfc2396.txt
	 *
	 * Thanks to Marco and Thomas
	 * @author Daniel Murphy
	 * @see http://web.archive.org/web/20130115153639/http://www.dmurph.com/2011/01/java-uri-encoder/
	 **/
	public static String encodeURI(String argString) {
		StringBuilder uri = new StringBuilder();
		
		char[] chars = argString.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| MARK.indexOf(c) != -1) {
				uri.append(c);
			} else {
				appendEscaped(uri, c);
			}
		}
		return uri.toString();
	}
	
	/**
	 * Reverse function for encodeURI. Basically it decodes into UTF-8
	 * @param argString as String
	 * @return String
	 **/
	public static String decodeURI(String argString) {
		try {
			return java.net.URLDecoder.decode(argString, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sanatizes HTML escape characters "<", ">", or "&" along with "\" escape
	 * This covers most cases of HTML injection in a child html, but not as a property value
	 *
	 * @TODO : Optimize this as a single parse? because this function is called to sanatize almost every API request
	 *
	 * @param  Input string to sanatize
	 *
	 * @return Sanatized string
	 **/
	public static String commonHtmlEscapeCharacters(String input) {
		String ret = input;
		
		// This has to go first, before other escape
		ret = ret.replaceAll("\\&", "&amp;");
		
		// Common XML / HTML open close brackets
		ret = ret.replaceAll("\\<", "&#60;");
		ret = ret.replaceAll("\\>", "&#62;");
		
		//ret = ret.replaceAll("\\`", "&#96;");
		//ret = ret.replaceAll("\\'", "&#8216;");
		//ret = ret.replaceAll("\\\"", "&#34;"); //Removing quote sanitisation as SQL security happens on another layer
		
		// Common string \ escape, relevent for embeded javascript in HTML
		ret = ret.replaceAll("\\\\", "&#92;");
		return ret;
	}
	
	/**
	 * Helper function, used to escape an object in accordance to its instanceof type
	 */
	@SuppressWarnings("unchecked")
	protected static Object commonHtmlEscapeCharacters_generic(Object v) {
		// Sanatize if needed
		if (v instanceof String) {
			v = commonHtmlEscapeCharacters((String) v);
		} else if (v instanceof Map) {
			v = commonHtmlEscapeCharacters((Map<String, Object>) v);
		} else if (v instanceof List) {
			v = commonHtmlEscapeCharacters((List<Object>) v);
		}
		return v;
	}
	
	/**
	 * Sanatizes HTML escape characters "<", ">", or "&" along with "\" escape
	 * This covers most cases of HTML injection in a child html, but not as a property value
	 *
	 * @param  Map of values to sanatize, note that keys are NOT sanatized
	 *
	 * @return Sanatized Map
	 **/
	public static Map<String, Object> commonHtmlEscapeCharacters(Map<String, Object> input) {
		// Result map
		Map<String, Object> res = new HashMap<String, Object>();
		
		// Iterate and update
		for (String k : input.keySet()) {
			res.put(k, commonHtmlEscapeCharacters_generic(input.get(k)));
		}
		
		// Return modified input values
		return res;
	}
	
	/**
	 * Sanatizes HTML escape characters "<", ">", or "&" along with "\" escape
	 * This covers most cases of HTML injection in a child html, but not as a property value
	 *
	 * @param  Map of values to sanatize, note that keys are NOT sanatized
	 *
	 * @return Sanatized List
	 **/
	public static List<Object> commonHtmlEscapeCharacters(List<Object> input) {
		// Result list
		List<Object> res = new ArrayList<Object>();
		
		// Iterate and update
		for (int i = 0; i < input.size(); i++) {
			res.add(commonHtmlEscapeCharacters_generic(input.get(i)));
		}
		
		// Return modified input values
		return res;
	}
}
