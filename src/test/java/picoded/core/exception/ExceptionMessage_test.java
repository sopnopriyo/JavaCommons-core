package picoded.core.exception;

import org.junit.Test;

public class ExceptionMessage_test {
	
	//
	// Expected exception testing
	//
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new ExceptionMessage();
	}
	
}
