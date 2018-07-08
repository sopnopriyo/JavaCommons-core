package picoded.core.struct;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.Set;
import java.util.HashSet;

import picoded.core.conv.GenericConvert;
import picoded.core.conv.NestedObjectUtil;
import picoded.core.exception.ExceptionMessage;
import picoded.core.struct.template.UnsupportedDefaultMap;

/**
 * Generic convert support for a value boxing / holder / wrapper
 * 
 * This is used primarily to provide some underlying value the full GenericConvert interface convinence
 **/
public interface GenericConvertValue<V> {
	
	// Put / get / remove value options to overwrite
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * [Needs to be overriden, currently throws UnsupportedOperationException]
	 * 
	 * Update the currently stored value (if supported)
	 * 
	 * @param  value to update to
	 **/
	default void putValue(V value) {
		throw new UnsupportedOperationException(ExceptionMessage.functionNotImplemented);
	}
	
	/**
	 * [Needs to be overriden, currently throws UnsupportedOperationException]
	 * 
	 * Remove the currently stored value (if supported)
	 **/
	default void removeValue() {
		throw new UnsupportedOperationException(ExceptionMessage.functionNotImplemented);
	}
	
	/**
	 * [Needs to be overriden, currently throws UnsupportedOperationException]
	 * 
	 * Get and return its stored value.
	 *
	 * @return value used for generic convert where applicable
	 **/
	default V getValue() {
		throw new UnsupportedOperationException(ExceptionMessage.functionNotImplemented);
	}
	
	// Get value call with fallback
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * Get and return its stored value
	 * 
	 * @param fallbck The fallback default if get returns null
	 *
	 * @return value used for generic convert where applicable
	 **/
	default V getValue(V fallbck) {
		V ret = getValue();
		if (ret != null) {
			return ret;
		}
		return fallbck;
	}
	
	// to string conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To String conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable, aka null)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default String getString(String fallbck) {
		return GenericConvert.toString(getValue(), fallbck);
	}
	
	/**
	 * Default null fallback, To String conversion of generic value
	 *
	 * @return The converted string, always possible unless null
	 **/
	default String getString() {
		return GenericConvert.toString(getValue());
	}
	
	// to boolean conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To boolean conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default boolean getBoolean(boolean fallbck) {
		return GenericConvert.toBoolean(getValue(), fallbck);
	}
	
	/**
	 * Default boolean fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default boolean getBoolean() {
		return GenericConvert.toBoolean(getValue());
	}
	
	// to Number conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To Number conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default Number getNumber(Number fallbck) {
		return GenericConvert.toNumber(getValue(), fallbck);
	}
	
	/**
	 * Default Number fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default Number getNumber() {
		return GenericConvert.toNumber(getValue());
	}
	
	// to int conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To int conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default int getInt(int fallbck) {
		return GenericConvert.toInt(getValue(), fallbck);
	}
	
	/**
	 * Default int fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default int getInt() {
		return GenericConvert.toInt(getValue());
	}
	
	// to long conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To long conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default long getLong(long fallbck) {
		return GenericConvert.toLong(getValue(), fallbck);
	}
	
	/**
	 * Default long fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default long getLong() {
		return GenericConvert.toLong(getValue());
	}
	
	// to float conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To float conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default float getFloat(float fallbck) {
		return GenericConvert.toFloat(getValue(), fallbck);
	}
	
	/**
	 * Default float fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default float getFloat() {
		return GenericConvert.toFloat(getValue());
	}
	
	// to double conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To double conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default double getDouble(double fallbck) {
		return GenericConvert.toDouble(getValue(), fallbck);
	}
	
	/**
	 * Default float fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default double getDouble() {
		return GenericConvert.toDouble(getValue());
	}
	
	// to byte conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To byte conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default byte getByte(byte fallbck) {
		return GenericConvert.toByte(getValue(), fallbck);
	}
	
	/**
	 * Default float fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default byte getByte() {
		return GenericConvert.toByte(getValue());
	}
	
	// to short conversion
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To short conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted string, always possible unless null
	 **/
	default short getShort(short fallbck) {
		return GenericConvert.toShort(getValue(), fallbck);
	}
	
	/**
	 * Default short fallback, To String conversion of generic object
	 *
	 * @return The converted string, always possible unless null
	 **/
	default short getShort() {
		return GenericConvert.toShort(getValue());
	}
	
	// to UUID / GUID
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To UUID conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted UUID, always possible unless null
	 **/
	default UUID getUUID(Object fallbck) {
		return GenericConvert.toUUID(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To UUID conversion of generic object
	 *
	 * @param input The input value to convert
	 *
	 * @return The converted value
	 **/
	default UUID getUUID() {
		return GenericConvert.toUUID(getValue());
	}
	
	/**
	 * To GUID conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted UUID, always possible unless null
	 **/
	default String getGUID(Object fallbck) {
		return GenericConvert.toGUID(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To GUID conversion of generic object
	 *
	 * @param input The input value to convert
	 *
	 * @return The converted value
	 **/
	default String getGUID() {
		return GenericConvert.toGUID(getValue());
	}
	
	/**
	 * To List<Object> conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted Object[], always possible unless null
	 **/
	default List<Object> getObjectList(Object fallbck) {
		return GenericConvert.toList(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To List<Object> conversion of generic object
	 *
	 * @param input The input value to convert
	 *
	 * @default The converted value
	 **/
	default List<Object> getObjectList() {
		return GenericConvert.toList(getValue());
	}
	
	/// To List<V> conversion of generic object
	///
	/// @param fallbck The fallback default (if not convertable)
	///
	/// @returns The converted Object[], always possible unless null
	default <V> List<V> getList(Object fallbck) {
		return GenericConvert.toList(getValue(), fallbck);
	}
	
	/// Default Null fallback, To List<V> conversion of generic object
	///
	/// @param input The input value to convert
	///
	/// @default The converted value
	default <V> List<V> getList() {
		return GenericConvert.toList(getValue());
	}
	
	// to map
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To String Map conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted Map if possible, else null
	 **/
	default <K extends String, V> Map<K, V> getStringMap(Object fallbck) {
		return GenericConvert.toStringMap(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To String Map conversion of generic object
	 *
	 * @return The converted Map if possible, else null
	 **/
	default <K extends String, V> Map<K, V> getStringMap() {
		return GenericConvert.toStringMap(getValue());
	}
	
	/**
	 * To String Map conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted Map if possible, else null
	 **/
	default <K extends String, V> GenericConvertMap<K, V> getGenericConvertStringMap(Object fallbck) {
		return GenericConvert.toGenericConvertStringMap(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To String Map conversion of generic object
	 *
	 * @return The converted Map if possible, else null
	 **/
	default <K extends String, V> GenericConvertMap<K, V> getGenericConvertStringMap() {
		return GenericConvert.toGenericConvertStringMap(getValue());
	}
	
	// to array
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To String Map conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted Map if possible, else null
	 **/
	default GenericConvertList<V> getGenericConvertList(Object fallbck) {
		return GenericConvert.toGenericConvertList(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To String Map conversion of generic object
	 *
	 * @return The converted Map if possible, else null
	 **/
	default GenericConvertList<V> getGenericConvertList() {
		return GenericConvert.toGenericConvertList(getValue());
	}
	
	// to string array
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To String[] conversion of generic object
	 *
	 * @param The fallback default (if not convertable)
	 *
	 * @return The converted String[], always possible unless null
	 **/
	default String[] getStringArray(Object fallbck) {
		return GenericConvert.toStringArray(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To String[] conversion of generic object
	 *
	 * @param input The input value to convert
	 *
	 * @return The converted value
	 **/
	default String[] getStringArray() {
		return getStringArray(null);
	}
	
	// to object array
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * To Object[] conversion of generic object
	 *
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The converted Object[], always possible unless null
	 **/
	default Object[] getObjectArray(Object fallbck) {
		return GenericConvert.toObjectArray(getValue(), fallbck);
	}
	
	/**
	 * Default Null fallback, To Object[] conversion of generic object
	 *
	 * @param input The input value to convert
	 *
	 * @default The converted value
	 **/
	default Object[] getObjectArray() {
		return GenericConvert.toObjectArray(getValue());
	}
	
}
