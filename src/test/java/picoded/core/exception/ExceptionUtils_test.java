package picoded.core.exception;

import org.junit.Test;

public class ExceptionUtils_test {
	
	//
	// Expected exception testing
	//
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new ExceptionUtils();
	}
	
}
