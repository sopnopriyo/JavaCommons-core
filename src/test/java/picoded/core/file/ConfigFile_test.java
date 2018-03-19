package picoded.core.file;

import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
 * Test Case for picoded.core.file.ConfigFile
 */
public class ConfigFile_test {

    // Test directories and setup
    //----------------------------------------------------------------------------------------------------
    public static String testDirStr = "./test/ConfigFile/";

    @Test
    public void positiveCases () {
        ConfigFile configFile = new ConfigFile(new File(testDirStr,"sample.json"));
        assertEquals("John", configFile.getString("name"));
        assertEquals(55, configFile.getInt("age"));
    }

    @Test(expected = RuntimeException.class)
    public void negativeCases(){
        ConfigFile configFile = new ConfigFile(new File(testDirStr, "nonJsonFile.yaml"));
    }
}
