package picoded.core.file;

//
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import picoded.core.struct.GenericConvertMap;
import picoded.core.conv.ConvertJSON;
import picoded.core.conv.GenericConvert;
import picoded.core.conv.NestedObjectUtil;
import picoded.core.conv.NestedObjectFetch;

/**
 * Loads a config folder, consisting of JSON / HTML files.
 * 
 * And making its various values avaliable. 
 * 
 * Note that get/fetch result is meant to be mainly "read only",
 * any modification may result into unexpected behaviour.
 * 
 * Note that internally this uses a ConcurrentHashMap for folders.
 * As such one could initialize this once on context start of a server,
 * and read it safely across multiple threads.
 **/
public class ConfigFileSet implements GenericConvertMap<String, Object> {
	
	//-----------------------------------------------------------------------------------
	//
	// Constructors
	//
	//-----------------------------------------------------------------------------------
	
	// Logger to use, for config file warnings
	private static final Logger LOGGER = Logger.getLogger(ConfigFileSet.class.getName());
	
	// The actual internal config file mapping
	protected ConcurrentHashMap<String, Object> configMap = new ConcurrentHashMap<String, Object>();
	
	/**
	 * Blank constructor
	 **/
	public ConfigFileSet() {
	}
	
	/**
	 * Constructor with the default file path to scan
	 * 
	 * @param  dirPath directory to perform scan recursively for html/json files
	 **/
	public ConfigFileSet(String dirPath) {
		addConfigSet(dirPath);
	}
	
	/**
	 * Constructor with the default file path to scan
	 * 
	 * @param  dirPath directory to perform scan recursively for html/json files
	 **/
	public ConfigFileSet(File dirPath) {
		addConfigSet(dirPath);
	}
	
	//-----------------------------------------------------------------------------------
	//
	// Config File folder importing
	//
	//-----------------------------------------------------------------------------------
	
	/**
	 * Scans the given directory, and add it to the existing configuration mapping
	 * 
	 * @param  dirPath directory to perform scan recursively for html/json files
	 */
	public void addConfigSet(File dirPath) {
		// Throw an exception, if config set is not a directory
		if (!dirPath.isDirectory()) {
			throw new IllegalArgumentException("Expected a directory path for : "
				+ dirPath.getAbsolutePath());
		}
		
		// Iterate its child files, and add them to the config map
		File[] innerFiles = dirPath.listFiles();
		for (File innerFile : innerFiles) {
			addConfigSubSetToMap(innerFile, configMap);
		}
	}
	
	/**
	 * Scans the given directory, and add it to the existing configuration mapping
	 * 
	 * @param  dirPath directory to perform scan recursively for html/json files
	 */
	public void addConfigSet(String dirPath) {
		addConfigSet(new File(dirPath));
	}
	
	/**
	 * Add either a json file as a config object, or scan a folder for config objects.
	 * This is done recursively, creating a ConcurrentHashMap (if needed) for each submap.
	 * 
	 * @param inFile  that represents either a json file, or a folder to add
	 * @param map     the current folder (or configMap for root) map representation
	 */
	private void addConfigSubSetToMap(File inFile, ConcurrentHashMap<String, Object> map) {
		// Input file name to use
		String fileName = inFile.getName();
		
		// Check if file or directory is used
		if (inFile.isDirectory()) {
			
			//
			// Its a directory - Generate the submap to use
			//
			ConcurrentHashMap<String, Object> submap;
			
			// Use an existing submap if possible
			Object currentMap = map.get(fileName);
			if (currentMap instanceof ConcurrentHashMap) {
				// Use an existing configured concurrent map
				submap = (ConcurrentHashMap<String, Object>) currentMap;
			} else if (currentMap instanceof Map) {
				// Convert a previous config, to a concurrent map (folder)
				submap = new ConcurrentHashMap<>();
				submap.putAll((Map<String, Object>) currentMap);
			} else {
				// Assuming no existing folder configured, init it
				submap = new ConcurrentHashMap<>();
			}
			
			//
			// Scan the directory, recursively
			//
			File[] innerFiles = inFile.listFiles();
			for (File innerFile : innerFiles) {
				addConfigSubSetToMap(innerFile, submap);
			}
			
			// Store the directory map
			map.put(fileName, submap);
		} else {
			
			//
			// Object is a file, store it accordingly if its a valid file
			// For now, get the file prefix an extension to use
			//
			String filePrefix = null;
			String fileExtension = null;
			int endingDot = fileName.lastIndexOf('.');
			if (endingDot > 0) {
				fileExtension = fileName.substring(endingDot + 1);
				filePrefix = fileName.substring(0, endingDot);
			}
			
			//
			// Store the data according to its format
			// if possible
			//
			try {
				if ( //
				fileExtension.equalsIgnoreCase("json") || //
					fileExtension.equalsIgnoreCase("js") //
				) {
					//
					// Takes in a JS / JSON file, and map it accordingly
					//
					String jsString = FileUtil.readFileToString(inFile);
					map.put(filePrefix, ConvertJSON.toObject(jsString));
				} else if ( //
				fileExtension.equalsIgnoreCase("html") //
				) {
					//
					// Takes in a HTML file, and store it as it is
					//
					map.put(fileName, FileUtil.readFileToString(inFile));
				}
			} catch (Exception e) {
				LOGGER.warning("[SKIP] Failed to load config file (invalid format?) : "
					+ inFile.getAbsolutePath());
			}
		}
	}
	
	//-----------------------------------------------------------------------------------
	//
	// KeySet and fetch overwrite handling
	//
	//-----------------------------------------------------------------------------------
	
	/**
	 * Top layer keySet fetching
	 **/
	public Set<String> keySet() {
		return NestedObjectUtil.filterKeySet(configMap.keySet());
	}
	
	/**
	 * Map get function, overwritten as a fetch
	 *
	 * @param key The input value key to convert
	 *
	 * @return The converted string, always possible unless null
	 **/
	@Override
	public Object get(Object key) {
		return fetchObject(key.toString(), null);
	}
	
	/**
	 * Gets an object from the map,
	 * That could very well be, a map inside a list, inside a map, inside a
	 * .....
	 *
	 * Note that at each iteration step, it attempts to do a FULL key match
	 * first, before the next iteration depth.
	 *
	 * @param base Map / List to manipulate from
	 * @param key The input key to fetch, possibly nested
	 * @param fallbck The fallback default (if not convertable)
	 *
	 * @return The fetched object, always possible unless fallbck null
	 **/
	@Override
	public Object fetchObject(String key, Object fallbck) {
		return NestedObjectFetch.fetchObject(configMap, key, fallbck);
	}
	
}
