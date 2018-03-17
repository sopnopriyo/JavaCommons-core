package picoded.core.file;

import java.io.File;
import java.util.*;

import picoded.core.conv.GenericConvert;
import picoded.core.struct.GenericConvertHashMap;

/**
 * Config file loader
 *
 * Takes in an JSON file, gives out a map.
 * 
 * NOTE: This deprecate INI file support
 *
 * @TO-DO
 * + Unit test, extended functions
 *
 * + Variable subsitution (use key values as key names)
 *   eg: sys.${sys.selectedStack; default}.database
 * + Array support
 *   eg: sys.dbStack[0].database
 * + Nested substitution
 *   eg: sys.${sys.selectedStack; sys.default}.database
 *
 * + JSON files delayed load
 * + Spliting INI, and JSON load into its own seperate class,
 *   use ConfigFileSet to switch between classes on setup
 *
 * @TO-CONSIDER
 * + Case insensitive key names?
 * + File write????
 **/
public class ConfigFile extends GenericConvertHashMap<String, Object> {
	
	/**
	 * The actual inner map storage
	 **/
	
	/**
	 * Blank constructor
	 **/
	protected ConfigFile() {
	}
	
	/**
	 * Constructor, which takes in an file object and stores it
	 **/
	public ConfigFile(File fileObj) {
		innerConstructor(fileObj);
	}
	
	/**
	 * Constructor, which takes in an file path and stores it
	 **/
	public ConfigFile(String filePath) {
		innerConstructor(new File(filePath));
	}
	
	//-------------------------------------------------
	//
	// File name handling
	//
	//-------------------------------------------------

	/**
	 * The config file name
	 **/
	String fileName = "";
	
	/**
	 * Filename of the 
	 **/
	public String fileName() {
		return fileName;
	}

	//-------------------------------------------------
	//
	// Config file loading
	//
	//-------------------------------------------------

	/**
	 * Inner constructor which extracts out the JSON content
	 * from the file into the internal map
	 */
	private void innerConstructor(File inFile) {
		// The json map to load
		Map<String, Object> jsonMap = null;
		
		// Load the respective file as a map
		fileName = inFile.getName();
		if (fileName.endsWith(".js") || fileName.endsWith(".json")) {
			String jsString = FileUtil.readFileToString(inFile);
			jsonMap = GenericConvert.toGenericConvertStringMap(jsString,
				new HashMap<String, Object>());
		} else if (fileName.endsWith(".html")) {
			String jsString = FileUtil.readFileToString(inFile);
			jsonMap = new HashMap<String, Object>();
			jsonMap.put("html", jsString);
		} 
		
		// Process the loaded jsonMap
		if(jsonMap != null) {
			// JSON Map loaded, store data into the current internal structure
			this.putAll(jsonMap);
		} else {
			// Assume unsupported file type
			throw new RuntimeException("Unsupported file type : " + fileName);
		}
	}
	
}
