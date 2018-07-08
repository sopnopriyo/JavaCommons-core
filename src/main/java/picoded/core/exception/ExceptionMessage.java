package picoded.core.exception;

/**
 * Utility static class of common reusable exception messages, or its message builders. You know those that happen all too often.
 **/
public class ExceptionMessage {
	
	/**
	 * Invalid constructor (throws exception)
	 **/
	protected ExceptionMessage() {
		throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	}
	
	//
	// Developers note
	//
	// We intentionally did not initaite instances of exception inside here to be easily thrown. For example.
	// `public static String staticClassConstructorException = new IllegalAccessError(ExceptionMessage.staticClassConstructor);`
	// As this will mess up the root trace of the stack trace into this class.
	//
	
	/** 
	 * Static / Utility class exception string message, should be used inside their respective constructor as an "IllegalAccessError" exception
	 * 
	 * ```
	 * throw new IllegalAccessError(ExceptionMessage.staticClassConstructor);
	 * ```
	 **/
	public static String staticClassConstructor = "Static / Utility class - instances should NOT be constructed in standard programming";
	
	/** 
	 * Function not (yet?) implemented.
	 * 
	 * ```
	 * throw new UnsupportedOperationException(ExceptionMessage.functionNotImplemented);
	 * ```
	 **/
	public static String functionNotImplemented = "function not (yet?) implemented";
}