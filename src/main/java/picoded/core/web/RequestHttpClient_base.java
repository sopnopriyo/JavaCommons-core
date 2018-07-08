package picoded.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import picoded.core.conv.NestedObjectUtil;
import picoded.core.conv.StringEscape;
import picoded.core.conv.MapValueConv;
import picoded.core.conv.ConvertJSON;
import picoded.core.struct.GenericConvertHashMap;
import picoded.core.struct.GenericConvertMap;
import picoded.core.common.EmptyArray;

/**
 * RequestHttpClient base instance implmentation of RequestHttpClient
 * 
 * This is mainly done to keep the OkHttp code module more "private"
 * and overall smaller class files sizes.
 **/
class RequestHttpClient_base {
	
	//------------------------------------------------
	//
	//  OKHTTP MediaType variables
	//
	//------------------------------------------------
	
	public static final MediaType MEDIATYPE_JSON = MediaType
		.parse("application/json; charset=utf-8");
	public static final MediaType MEDIATYPE_OCTETSTREAM = MediaType
		.parse("application/octet-stream");
	
	//------------------------------------------------
	//
	//  Constructor
	//
	//------------------------------------------------
	
	/**
	 * Setup the RequestHttpClient with default configuration settings
	 */
	public RequestHttpClient_base() {
		// Does a blank config setup
		config = new GenericConvertHashMap<>();
		client = builderSetup(new OkHttpClient.Builder(), config).build();
	}
	
	/**
	 * Setup the RequestHttpClient with custom configuration settings
	 * 
	 * @param  config map to be used, can be null
	 */
	public RequestHttpClient_base(Map<String, Object> inConfig) {
		// Does a config based setup
		config = new GenericConvertHashMap<>(
			(Map<String, Object>) (NestedObjectUtil.deepCopy(inConfig)));
		client = builderSetup(new OkHttpClient.Builder(), config).build();
	}
	
	// // @TODO : Reconsider if this constructor is needed
	// /**
	//  * Setup the RequestHttpClient with custom configuration settings,
	//  * extending from an existing connection pool
	//  * 
	//  * @param  config map to be used, can be null
	//  */
	// public RequestHttpClient(RequestHttpClient base, Map<String,Object> inConfig) {
	// 	// Does an extension config setup
	// 	client = builderSetup(base.client.newBuilder(), inConfig).build();
	// }
	
	//------------------------------------------------
	//
	//  OkHttpClient setup
	//
	//------------------------------------------------
	
	// Internal implementation
	protected OkHttpClient client;
	
	// Internal implementation config
	protected GenericConvertMap<String, Object> config;
	
	/**
	 * Builder specific setup, where the configuration settings are applied
	 * 
	 * @param  builder object to modify
	 * @param  config map to be used, cannot be null
	 */
	static protected OkHttpClient.Builder builderSetup(OkHttpClient.Builder builder,
		GenericConvertMap<String, Object> config) {
		
		//
		// Apply actual configuration settings
		//
		
		// Connection idle pool
		builder.connectionPool(new ConnectionPool(config.getInt("idleCount", 10), config.getLong(
			"idleTimeout", 300 * 1000), TimeUnit.MILLISECONDS));
		
		// Connection timeout settings
		builder.connectTimeout(config.getLong("connectTimeout", 10 * 1000), TimeUnit.MILLISECONDS);
		
		// Read timeout settings
		builder.connectTimeout(config.getLong("readTimeout", 30 * 1000), TimeUnit.MILLISECONDS);
		
		// Write timeout settings
		builder.connectTimeout(config.getLong("writeTimeout", 30 * 1000), TimeUnit.MILLISECONDS);
		
		//
		// Return OkHttpClient.Builder
		//
		return builder;
	}
	
	//------------------------------------------------
	//
	//  Setup reconfiguration
	//
	//------------------------------------------------
	
	/**
	 * Setup and reconfigure if needed the client
	 * 
	 * @param  config map to be used, can be null
	 */
	public void setup(Map<String, Object> inConfig) {
		// Update the configuration map
		config.putAll(inConfig);
		// Rebuild the client connection
		client = builderSetup(client.newBuilder(), config).build();
	}
	
	//------------------------------------------------
	//
	//  cookies, and headers operation handling
	//
	//------------------------------------------------
	
	/**
	 * Utility function used to string encode parameters,
	 * used either for cookie handling or GET parameters
	 * 
	 * @param  paramMap  to convert into a string
	 * @param  seperator between key value pair
	 *                   - Use "&" for GET parameters
	 *                   - Use "; " for cookie parameters
	 *
	 * Examples
	 * ```
	 * Map<String,String[]> param = new HashMap<>();
	 * param.put("hello", new String[] { "one", "two" });
	 * param.put("a", new String[] { "b" });
	 *
	 * httpEncodeMap(param, "&");  // a=b&hello=one&hello=two
	 * httpEncodeMap(param, "; "); // a=b; hello=one; hello=two
	 * ```
	 * @return  encoded map as a single string, return null if paramMap is empty
	 */
	protected static String httpEncodeMap(Map<String, String[]> params, String seperator) {
		// Return null if no valid values are present
		if (params == null || params.size() <= 0) {
			return null;
		}
		
		// Get the parameter keys, this is intentionally sorted
		// to help optimize the request for cache systems
		List<String> keys = new ArrayList<>(params.keySet());
		Collections.sort(keys);
		
		// The resulting string builder
		StringBuilder res = new StringBuilder();
		
		// Flag used to indicate the first parameter is being processed 
		// (and should not have the "seperator" prefixed)
		boolean first = true;
		
		// Convert each key value, into GET parameters
		for (String key : keys) {
			for (String val : params.get(key)) {
				if (!first) {
					//add to previous paremeters
					res.append(seperator);
				}
				
				// Encode the string value
				res.append(StringEscape.encodeURI(key));
				res.append("=");
				res.append(StringEscape.encodeURI(val));
				
				// Disable the first parameter flag
				first = false;
			}
		}
		
		// Return the final result string
		return res.toString();
	}
	
	/**
	 * Setting up the header of the request
	 * 
	 * @param reqBuilder to add the header into
	 * @param cookieMap  to add into the request builder
	 * @param headerMap  to add into the request builder
	 */
	protected static Request.Builder setupRequestHeaders( //
		Request.Builder reqBuilder, //
		Map<String, String[]> cookieMap, //
		Map<String, String[]> headerMap //
	) {
		// Add the cookie if its valid
		//-------------------------------------------
		
		// Compute the cookie string
		String cookieStr = httpEncodeMap(cookieMap, "; ");
		
		// Add the cookie string if its valid
		if (cookieStr != null && cookieStr.length() > 0) {
			reqBuilder.addHeader("Cookie", cookieStr);
		}
		
		// Add the header if its valid
		//-------------------------------------------
		
		// Terminate early if header is null
		if (headerMap == null || headerMap.size() <= 0) {
			return reqBuilder;
		}
		
		// Get the header keys, this is intentionally sorted
		// to help optimize the request for cache systems
		List<String> keys = new ArrayList<>(headerMap.keySet());
		Collections.sort(keys);
		
		// Iterate each key and send the headers over
		for (String key : keys) {
			for (String val : headerMap.get(key)) {
				reqBuilder.addHeader(key, val);
			}
		}
		
		// Return with built header
		return reqBuilder;
	}
	
	//------------------------------------------------
	//
	//  GET request and parameter handling
	//
	//------------------------------------------------
	
	/**
	 * Appends the GET request URL with the parameters if needed
	 * 
	 * @param  reqUrl to use
	 * @param  paramMap to include into the output URL
	 * 
	 * @return reqUrl with the parameter appeneded (if needed)
	 **/
	protected static String appendGetParameters(String reqURL, Map<String, String[]> paramMap) {
		// trim the requestUrl string, and encode the GET parameters
		reqURL = reqURL.trim();
		String getEncodedParams = httpEncodeMap(paramMap, "&");
		
		// Return without modification if parameters are not provided
		if (getEncodedParams == null) {
			return reqURL;
		}
		
		// Converts to string builder, appending query delimiter if needed
		StringBuilder req = new StringBuilder(reqURL);
		if (reqURL.endsWith("?")) {
			//does nothing
		} else if (reqURL.indexOf('?') >= 0) {
			req.append("&"); //add to previous paremeters
		} else {
			req.append("?"); //start of parameters
		}
		
		// Append the getEncoded param
		req.append(getEncodedParams);
		
		// Return the complete URL with get parameters
		return req.toString();
	}
	
	/**
	 * Performs GET request : with parameters, appended to the requestURL
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpGet( //
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) { //
		 // Process the get parameters
		reqUrl = appendGetParameters(reqUrl, paramMap);
		
		// Build the request
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);
		return executeRequestBuilder(reqBuilder);
	}
	
	//------------------------------------------------
	//
	//  POST request Form / Multipart / JSON
	//
	//------------------------------------------------
	
	/**
	 * Performs POST request : with form parameters as the body
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPost(//
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeFormRequest("POST", reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Performs POST request : using multipart
	 *
	 * @param   Request URL to call
	 * @param   paramsMap  [can be null] Parameters to add to the request body,
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPostMultipart(//
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeMultipartRequest("POST", reqUrl, paramsMap, filesMap, cookiesMap, headersMap);
	}
	
	/**
	 * Performs POST request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   params     [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPostJSON(//
		String reqUrl, //
		Object jsonObj, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		try {
			return executeJsonRequest("POST", reqUrl, jsonObj, cookiesMap, headersMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//------------------------------------------------
	//
	//  PUT request Form / Multipart / JSON
	//
	//------------------------------------------------
	
	/**
	 * Performs PUT request : with form parameters as the body
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPutForm(//
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeFormRequest("PUT", reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Performs PUT request : using multipart
	 *
	 * @param   Request URL to call
	 * @param   paramsMap [can be null] Parameters to add to the request body,
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPutMultipart(//
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeMultipartRequest("PUT", reqUrl, paramsMap, filesMap, cookiesMap, headersMap);
	}
	
	/**
	 * Performs PUT request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   jsonObj    [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpPutJSON(String reqUrl, //
		Object jsonObj, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		try {
			return executeJsonRequest("PUT", reqUrl, jsonObj, cookiesMap, headersMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//------------------------------------------------
	//
	//  DELETE request and parameter handling
	//
	//------------------------------------------------
	
	/**
	 * Performs DELETE request : with form parameters as the body
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpDelete(//
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeFormRequest("DELETE", reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Performs DELETE request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   jsonObj    [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpDeleteJSON(String reqUrl, //
		Object jsonObj, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		try {
			return executeJsonRequest("DELETE", reqUrl, jsonObj, cookiesMap, headersMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Performs DELETE request : using multipart
	 *
	 * @param   Request URL to call
	 * @param   paramsMap [can be null] Parameters to add to the request body,
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp httpDeleteMultipart(//
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		return executeMultipartRequest("DELETE", reqUrl, paramsMap, filesMap, cookiesMap, headersMap);
	}
	
	////////////////////////////////////////////////////////////////////////
	//
	// Util functions
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Executes the form request
	 *
	 * @param   method to be used to send the request (POST/PUT/DELETE)
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	private ResponseHttp executeFormRequest(//
		String method, //
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);
		
		if (paramMap != null) {
			// Create the form with the paramMap
			RequestBody requestBody = buildFormBody(paramMap);
			
			// Attach RequestBody to the RequestBuilder
			reqBuilder.method(method, requestBody);
		}
		
		return executeRequestBuilder(reqBuilder);
	}
	
	/**
	 * Executes the json string request
	 *
	 * @param   method to be used to send the request (POST/PUT/DELETE)
	 * @param   Request URL to call
	 * @param   json [can be null] JSON string / object to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	private ResponseHttp executeJsonRequest( //
		String method, //
		String reqUrl, //
		Object jsonObj, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);
		
		// Normalize json object to jsonString
		String jsonString = null;
		if (jsonObj == null) {
			jsonString = "";
		} else if (jsonObj instanceof String) {
			jsonString = (String) jsonObj;
		} else {
			jsonString = ConvertJSON.fromObject(jsonObj);
		}
		
		// Perform the json request
		RequestBody body = RequestBody.create(MEDIATYPE_JSON, jsonString);
		reqBuilder = reqBuilder.method(method, body);
		return executeRequestBuilder(reqBuilder);
	}
	
	/**
	 * Executes the multipart request
	 *
	 * @param   method to be used to send the request (POST/PUT/DELETE)
	 * @param   Request URL to call
	 * @param   paramsMap [can be null] Parameters to add to the request body,
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	private ResponseHttp executeMultipartRequest( //
		String method, //
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);
		
		if ((paramsMap != null && paramsMap.size() > 0) || (filesMap != null && filesMap.size() > 0)) {
			// Form multipart with the paramsMap and filesMap
			RequestBody requestBody = buildMultipartBody(paramsMap, filesMap);
			
			// Attach RequestBody to the RequestBuilder
			reqBuilder = reqBuilder.method(method, requestBody);
		}
		
		return executeRequestBuilder(reqBuilder);
	}
	
	/**
	 * Build and execute the request builder
	 *
	 * @param  reqBuilder with the body and method configured
	 * @return ResponseHttp containing the response
	 */
	private ResponseHttp executeRequestBuilder(Request.Builder reqBuilder) {
		// Build the request, and make the call
		try {
			Response response = client.newCall(reqBuilder.build()).execute();
			return new RequestHttpClient_response(response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generate the form body for okhttp to process
	 *
	 * @param paramMap the parameters to be passed
	 * @return Requestbody to be attached to Request.Builder
	 */
	private RequestBody buildFormBody(Map<String, String[]> paramMap) {
		// From the paramMap, create a RequestBody for attaching to the okhttp post method
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		for (String key : paramMap.keySet()) {
			String[] values = paramMap.get(key);
			for (String value : values) {
				formBodyBuilder.add(key, value);
			}
		}
		return formBodyBuilder.build();
	}
	
	/**
	 * Generate the multipart body for okhttp to process
	 *
	 * @param paramMap the parameters to be passed
	 * @param filesMap the key files map to be submitted
	 * @return Requestbody to be attached to Request.Builder
	 */
	private RequestBody buildMultipartBody(Map<String, String[]> paramMap,
		Map<String, File[]> filesMap) {
		MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
			.setType(MultipartBody.FORM);
		
		// With each param, add it to the form data part
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				String[] values = paramMap.get(key);
				for (String value : values) {
					multipartBuilder.addFormDataPart(key, value);
				}
			}
		}
		
		// for each file in the file array of the param, add it accordingly
		// to the form data part
		if (filesMap != null) {
			for (String key : filesMap.keySet()) {
				File[] files = filesMap.get(key);
				for (File file : files) {
					multipartBuilder.addFormDataPart(key, file.getName(),
						RequestBody.create(MEDIATYPE_OCTETSTREAM, file));
				}
			}
		}
		
		return multipartBuilder.build();
	}
	
	/**
	 * Convert Map<String, Object> into Map<String, String[]>
	 *
	 * @param mapToConvert of type Map<String, Object>
	 * @return Map<String, String[]>
	 */
	protected Map<String, String[]> convertMapObjectToStringArray(Map<String, Object> mapToConvert) {
		
		Map<String, String[]> reformedParamMap = null;
		
		if (mapToConvert != null) {
			reformedParamMap = new HashMap<String, String[]>();
			
			for (String key : mapToConvert.keySet()) {
				Object value = mapToConvert.get(key);
				if (value instanceof String) { // Convert to array of size 1
					reformedParamMap.put(key, new String[] { value.toString() });
				} else if (value instanceof String[]) { // Put the array back as it is
					reformedParamMap.put(key, (String[]) value);
				} else { // Convert using ConvertJSON as a string and put to array of size 1
					String convertedString = ConvertJSON.fromObject(value);
					reformedParamMap.put(key, new String[] { convertedString });
				}
			}
		}
		
		// Return the reformed map
		return reformedParamMap;
	}
	
	// /**
	//  * Sends a request with the respective HTTP / RequestBody type
	//  * 
	//  * @param reqUrl
	//  * @param reqType
	//  * @param paramType
	//  * @param paramMap
	//  * @param cookieMap
	//  * @param headerMap
	//  * @param fileMap
	//  * @param requestStream
	//  */
	
}