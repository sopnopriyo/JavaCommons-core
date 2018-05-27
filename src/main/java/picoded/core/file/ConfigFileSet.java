package picoded.core.file;

//
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import picoded.core.struct.GenericConvertMap;
import picoded.core.conv.ConvertJSON;
import picoded.core.conv.GenericConvert;
import picoded.core.conv.NestedObjectUtil;
import picoded.core.conv.NestedObjectFetch;

/**
 * Config file loader
 *
 * Iterates a filepath and loads all the various JSON config files.
 * 
 * 
 **/
public class ConfigFileSet implements GenericConvertMap<String, Object> {
	
	//-----------------------------------------------------------------------------------
	//
	// Constructors
	//
	//-----------------------------------------------------------------------------------
	
	// The actual internal config file mapping
	protected ConcurrentHashMap<String, Object> configMap = new ConcurrentHashMap<String, Object>();

	/**
	 * Blank constructor
	 **/
	public ConfigFileSet() {
		
	}
	
	/**
	 * Constructor with the default file path to scan
	 **/
	public ConfigFileSet(String filePath) {
		// addConfigSet(filePath);
	}
	
	/**
	 * Constructor with the default file path to scan
	 **/
	public ConfigFileSet(File filePath) {
		// addConfigSet(filePath);
	}
	
	//-----------------------------------------------------------------------------------
	//
	// Config File folder importing
	//
	//-----------------------------------------------------------------------------------

	/**
	 * Scans the given directory, and add it to the existing configuration mapping
	 * 
	 * @param  filePath directory to perform scan recursively for html/json files
	 */
	public void addConfigSet(File filePath) {
		addConfigSetToMap(filePath, configMap);
	}
	
	/**
	 * Scans the given directory, and add it to the existing configuration mapping
	 * 
	 * @param  filePath directory to perform scan recursively for html/json files
	 */
	public void addConfigSet(String filePath) {
		addConfigSet(new File(filePath));
	}
	
	/**
	 * Add either a json file as a config object, or scan a folder for config objects.
	 * This is done recursively, creating a ConcurrentHashMap (if needed) for each submap.
	 * 
	 * @param inFile  that represents either a json file, or a folder to add
	 * @param map     the current folder (or configMap for root) map representation
	 */
	private void addConfigSetToMap(File inFile, ConcurrentHashMap<String,Object> map) {
		// Input file name to use
		String fileName = inFile.getName();

		// Check if file or directory is used
		if (inFile.isDirectory()) {

			//
			// Its a directory - Generate the submap to use
			//
			ConcurrentHashMap<String,Object> submap;

			// Use an existing submap if possible
			Object currentMap = map.get(fileName) ;
			if(currentMap instanceof ConcurrentHashMap) {
				// Use an existing configured concurrent map
				submap = (ConcurrentHashMap<String,Object>)currentMap;
			} else if(currentMap instanceof Map) {
				// Convert a previous config, to a concurrent map (folder)
				submap = new ConcurrentHashMap<>();
				submap.putAll( (Map<String,Object>)currentMap );
			} else {
				// Assuming no existing folder configured, init it
				submap = new ConcurrentHashMap<>();
			}

			//
			// Scan the directory, recursively
			//
			File[] innerFiles = inFile.listFiles();
			for (File innerFile : innerFiles) {
				addConfigSetToMap(innerFile, submap);
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
				fileExtension = fileName.substring(endingDot+1);
				filePrefix = fileName.substring(0, endingDot);
			}

			//
			// Store the data according to its format
			//
			if ( //
				fileExtension.equalsIgnoreCase("json") || //
				fileExtension.equalsIgnoreCase("js") //
			) {
				//
				// Takes in a JS / JSON file, and map it accordingly
				//
				String jsString = FileUtil.readFileToString(inFile);
				map.put( filePrefix, ConvertJSON.toObject(jsString) );
			} else if( //
				fileExtension.equalsIgnoreCase("html") //
			) {
				//
				// Takes in a HTML file, and store it as it is
				//
				map.put( fileName, FileUtil.readFileToString(inFile) );
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
		return NestedObjectUtil.filterKeySet( configMap.keySet() );
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
