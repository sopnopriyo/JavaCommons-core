package picoded.core.conv;

import org.junit.Assert;
import org.junit.Test;

public class ArrayConv_test {
	
	//
	// Expected exception testing
	//
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new ArrayConv();
	}
	
	/// Sanity check, confirm that the apache commons method was inherited
	@Test
	public void ArrayUtilsBooleanAdd() {
		boolean[] ori = new boolean[] { true };
		boolean[] added = ArrayConv.add(ori, false);
		
		// Confirm the ArrayConv.add occured
		Assert.assertEquals(2, added.length);
		Assert.assertEquals(true, added[0]);
		Assert.assertEquals(false, added[1]);
	}
}
