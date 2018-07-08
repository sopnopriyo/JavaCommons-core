package picoded.core.file;

import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
 * Test Case for picoded.core.file.ConfigFile
 */
public class ConfigFileSet_test {
	
	// Test directories and setup
	//----------------------------------------------------------------------------------------------------
	public static String testDirStr = "./test/ConfigFile/";
	
	@Test
	public void positiveCases() {
		ConfigFileSet configFile = new ConfigFileSet(testDirStr);
		assertEquals("John", configFile.getString("sample.name"));
		assertEquals(55, configFile.getInt("sample.age"));
	}
}
