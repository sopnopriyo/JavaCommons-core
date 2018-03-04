package picoded.core.exception;

/**
 * Proxy to org.apache.commons.lang3.exception.ExceptionUtils. See its full documentation
 * <a href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/exception/ExceptionUtils.html">here</a>
 **/
public class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected ExceptionUtils() {
		throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	}
	
}