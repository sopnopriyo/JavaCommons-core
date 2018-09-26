package picoded.core.file;

// Test Case include
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.math.BigInteger;

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
		if( baseOutputDir.exists() ) {
			FileUtil.deleteDirectory(baseOutputDir); // Delete and clear directory (if it exists)
		}
		baseOutputDir.mkdirs(); // Remake directory
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

	@Test
	public void openInputStream_test() {
		File file = new File(testDir, "jsRegex.js");
		assertNotNull(FileUtilBase.openInputStream(file));
	}

	@Test(expected = Exception.class) 
	public void openInputStreamWithException_test() {
		File file = new File(testDir, "jsResgex.js");
		FileUtilBase.openInputStream(file);
	}

	@Test
	public void openOutputStream_test() {
		File file = new File(outputDirStr, "xyz.txt");
		assertNotNull(FileUtilBase.openOutputStream(file));
	}

	@Test (expected = RuntimeException.class)
	public void openOutputStreamWithException_test() throws FileNotFoundException{
		FileUtilBase.openOutputStream(outputDir);
	}

	@Test
	public void openOutputStreamAppend_test() {
		File file = new File(outputDirStr, "xyd.txt");
		assertNotNull(FileUtilBase.openOutputStream(file, true));
	}

	@Test (expected = RuntimeException.class)
	public void openOutputStreamAppendException_test() throws FileNotFoundException{
		FileUtilBase.openOutputStream(outputDir, true);
	}

	@Test
	public void byteCountToDisplaySizeFromBigInt_test() {
		String byteCount = FileUtilBase.byteCountToDisplaySize(new BigInteger("8599825996872482982482982252524684268426846846846846849848418418414141841841984219848941984218942894298421984286289228927948728929829"));
		String expectedValue = "7459160023045171927852192577984468773953346086956871620548078382502197662894449971823214090761499246629731202622063 EB";
		assertEquals(expectedValue, byteCount);
	}

	@Test (expected = RuntimeException.class)
	public void byteCountToDisplaySizeFromBigIntEx_test() {
		String byteCount = FileUtilBase.byteCountToDisplaySize(new BigInteger(""));
	}

	@Test
	public void byteCountToDisplaySizeFromLong_test() {
		long value = 82982252524618L;
		String byteCount = FileUtilBase.byteCountToDisplaySize(value);
		String expectedValue = "75 TB";
		assertEquals(expectedValue, byteCount);
	}

	// @Test (expected = RuntimeException.class)
	// public void byteCountToDisplaySizeFromLongEx_test() {
	// 	long value = 829822525246000000000018;
	// 	String byteCount = FileUtilBase.byteCountToDisplaySize(value);
	// }

	@Test
	public void touch_test() {
		File file = new File(outputDirStr, "hhe.txt");
		FileUtilBase.touch(file);
		assertNotNull(FileUtilBase.openInputStream(file));
	}

	@Test (expected = RuntimeException.class)
	public void touchEx_test() {
		FileUtilBase.touch(null);
	}

	@Test
	public void convertFileCollectionToFileArray_test() {
		File file = new File(outputDirStr, "hhee.txt");
		ArrayList<File> files = new ArrayList<File>();
		files.add(file);
		File[] fileArray = FileUtilBase.convertFileCollectionToFileArray(files);
		assertNotNull(fileArray);
	}

	// @Test (expected = RuntimeException.class)
	// public void convertFileCollectionToFileArraEx_test() {
	// 	ArrayList<File> files = new ArrayList<File>();
	// 	File[] fileArray = FileUtilBase.convertFileCollectionToFileArray(null);
	// }



}