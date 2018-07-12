package picoded.core.file;

// Test Case include
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import picoded.core.conv.GUID;

/**
 * Test Case for picoded.core.file.FileUtilBase
 */
public class FileUtilBase_test {

	// Test directories and setup
	//----------------------------------------------------------------------------------------------------
	public static String testDirStr = "./test/FileUtil/";
	public static File testDir = new File(testDirStr);
	
	public static String baseOutputDirStr = "./test/tmp/FileUtil/";
	public static File baseOutputDir = new File(baseOutputDirStr);
	
	// Setup as a randomised sub folder in setUp();
	public String outputDirStr = null;
	public File outputDir = null;
	
	/// Invalid constructor test
	@Test(expected = IllegalAccessError.class)
	public void invalidConstructor() throws Exception {
		new FileUtil();
	}
	
	@BeforeClass
	public static void baseSetup() throws IOException {
		FileUtil.deleteDirectory(baseOutputDir);
		baseOutputDir.mkdirs();
	}
	
	@Before
	public void setUp() {
		outputDirStr = baseOutputDirStr + GUID.base58() + "/";
		outputDir = new File(outputDirStr);
		outputDir.mkdirs();
	}

	@Test
	public void getFileWithNames_test() {
		assertNotNull(FileUtilBase.getFile(outputDir, new String[] {"hello.js"}));
	}

	@Test
	public void getFile_test() {
		assertNotNull(FileUtilBase.getFile(new String[] {"hello.js"}));
	}

	@Test
	public void getTempDirPath_test() {
		assertNotNull(FileUtilBase.getTempDirectoryPath());
	}

	@Test
	public void getTempDir_test() {
		assertNotNull(FileUtilBase.getTempDirectory());
	}

	@Test
	public void getUserDirectoryPath_test() {
		assertNotNull(FileUtilBase.getUserDirectoryPath());
	}

	@Test
	public void getUserDirectory_test() {
		assertNotNull(FileUtilBase.getUserDirectory());
	}

}