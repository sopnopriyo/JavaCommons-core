package picoded.core.conv;

import java.io.UnsupportedEncodingException;
import java.util.*;

import picoded.core.exception.ExceptionMessage;

/**
 * Utility library for string conversions.
 * 
 * Mainly from one format to another.
 * 
 * As of now it mainly consist of string and bytearray conversion. Strictly speaking
 * this can be easily done in native java. Hence this class is really just for convinence
 * to facilitate quick discovery and usage of the functionality, instead of digging up stack overflow.
 **/
public class StringConv {
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected StringConv() {
		throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	}
	
	// ByteArray conversion
	//--------------------------------------------------------------------------
	
	/**
	 * Convert from String to byte array
	 * 
	 * @param  input string to convert
	 * @param  charset to use, see java Charset documentation
	 * 
	 * @return  byte array representation of string, is null of input is null
	 */
	public static byte[] toByteArray(String input, String charset) {
		if (input == null) {
			return null;
		}
		
		try {
			return input.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convert from String to byte array, using the default UT-8 charset
	 * 
	 * @param  input string to convert, is null of input is null
	 * 
	 * @return  byte array representation of string
	 */
	public static byte[] toByteArray(String input) {
		return toByteArray(input, "UTF-8");
	}
	
	/**
	 * Convert from bytearray to String
	 * 
	 * @param  input byte array to convert
	 * @param  charset to use, see java Charset documentation
	 * 
	 * @return  string representation of byte array, is null of input is null
	 */
	public static String fromByteArray(byte[] input, String charset) {
		if (input == null) {
			return null;
		}
		try {
			return new String(input, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convert from bytearray to String, using the default UT-8 charset
	 * 
	 * @param  input byte array to convert
	 * 
	 * @return  string representation of byte array, is null of input is null
	 */
	public static String fromByteArray(byte[] input) {
		return fromByteArray(input, "UTF-8");
	}
	
}