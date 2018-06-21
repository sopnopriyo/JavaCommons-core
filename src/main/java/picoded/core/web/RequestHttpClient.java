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
 * RequestHttpClient instance, used in place of RequestHttp
 * when fine tuned configurations are required. And where instances need
 * to maintan its own connection pool (which is very unlikely)
 * 
 * Note : Internal implmentation is facilitated by OkHttpClient
 * 
 * The following is a list of possible JSON settings supported, and its default value.
 * 
 * ```
 * {
 * 	/// Maximum number of **idle** connection kept within connection pool
 *	/// for subsequent quick reuse
 * 	"idleCount" : 10,
 * 
 * 	/// Idle timeout, where connections are removed from the connection pool (in ms)
 * 	/// default is 300 seconds = 5 minutes
 * 	"idleTimeout" : 300 * 1000,
 * 
 * 	/// Connection timeout, this is for the HTTP handshake exchange operation (in ms)
 * 	/// default is 10 seconds
 * 	"connectTimeout" : 10 * 1000,
 * 
 * 	/// Read timeout, this is for each chunk of the HTTP request operation (in ms)
 * 	/// default is 30 seconds
 * 	"readTimeout" : 30 * 1000,
 * 
 * 	/// Write timeout, this is for each chunk of the HTTP request operation (in ms)
 * 	/// default is 30 seconds
 * 	"writeTimeout" : 30 * 1000
 * }
 * ```
 **/
public final class RequestHttpClient {

	//------------------------------------------------
	//
	//  OKHTTP variables
	//
	//------------------------------------------------
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");

	//------------------------------------------------
	//
	//  Constructor
	//
	//------------------------------------------------

	/**
	 * Setup the RequestHttpClient with default configuration settings
	 */
	public RequestHttpClient() {
		// Does a blank config setup
		config = new GenericConvertHashMap<>();
		client = builderSetup( new OkHttpClient.Builder(), config ).build();
	}

	/**
	 * Setup the RequestHttpClient with custom configuration settings
	 * 
	 * @param  config map to be used, can be null
	 */
	public RequestHttpClient(Map<String,Object> inConfig) {
		// Does a config based setup
		config = new GenericConvertHashMap<>( (Map<String,Object>)(NestedObjectUtil.deepCopy(inConfig)) );
		client = builderSetup( new OkHttpClient.Builder(), config ).build();
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
	protected GenericConvertMap<String,Object> config;

	/**
	 * Builder specific setup, where the configuration settings are applied
	 * 
	 * @param  builder object to modify
	 * @param  config map to be used, cannot be null
	 */
	static protected OkHttpClient.Builder builderSetup(OkHttpClient.Builder builder, GenericConvertMap<String,Object> config) {
		
		//
		// Apply actual configuration settings
		//

		// Connection idle pool
		builder.connectionPool( 
			new ConnectionPool(
				config.getInt("idleCount", 10), 
				config.getLong("idleTimeout", 300*1000), 
				TimeUnit.MILLISECONDS
			)
		);

		// Connection timeout settings
		builder.connectTimeout(
			config.getLong("connectTimeout", 10*1000),
			TimeUnit.MILLISECONDS
		);

		// Read timeout settings
		builder.connectTimeout(
			config.getLong("readTimeout", 30*1000),
			TimeUnit.MILLISECONDS
		);

		// Write timeout settings
		builder.connectTimeout(
			config.getLong("writeTimeout", 30*1000),
			TimeUnit.MILLISECONDS
		);

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
	public void setup(Map<String,Object> inConfig) {
		// Update the configuration map
		config.putAll(inConfig);
		// Rebuild the client connection
		client = builderSetup( client.newBuilder(), config ).build();
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
	protected static String httpEncodeMap(Map<String,String[]> params, String seperator) {
		// Return null if no valid values are present
		if(params == null || params.size() <= 0) {
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
		Map<String,String[]>cookieMap, //
		Map<String,String[]>headerMap //
	) {
		// Add the cookie if its valid
		//-------------------------------------------

		// Compute the cookie string
		String cookieStr = httpEncodeMap(cookieMap, "; ");

		// Add the cookie string if its valid
		if(cookieStr != null && cookieStr.length()>0) {
			reqBuilder.addHeader("Cookie", cookieStr);
		}
		
		// Add the header if its valid
		//-------------------------------------------

		// Terminate early if header is null
		if( headerMap == null || headerMap.size() <=0 ) {
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
		if(getEncodedParams == null)  {
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
	public ResponseHttp get( //
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

		// Build the request, and make the call
		try {
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

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
	public ResponseHttp postForm(//
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {

		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		if(paramMap != null){
			// Create the form with the paramMap
			RequestBody requestBody = buildFormBody(paramMap);

			// Attach RequestBody to the RequestBuilder
			reqBuilder.post(requestBody);
		}

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
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
	public ResponseHttp postJSON(//
		String reqUrl, //
		Object params, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		try{
			String jsonString = ConvertJSON.fromObject(params);
			return postJSON_string(
					reqUrl,
					jsonString,
					cookiesMap,
					headersMap
			);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs POST request : with json string as the parameter
	 *
	 * @param   Request URL to call
	 * @param   jsonString [can be null] JSON String to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp postJSON_string(//
		String reqUrl, //
		String jsonString, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		////////////////////////////////////////////////////////////////////////

		// Ensure jsonString is not null
		jsonString = (jsonString != null) ? jsonString : "";

		RequestBody body = RequestBody.create(JSON, jsonString);
		reqBuilder = reqBuilder.post(body);

		////////////////////////////////////////////////////////////////////////

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs POST request : using multipart
	 *
	 * @param   Request URL to call
	 * @param   paramsMap  [can be null] Parameters to add to the request body,
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @param   filesMap   [can be null] Files to add to the request body
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp postMultipart(//
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap, //
		Map<String, File[]> filesMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		////////////////////////////////////////////////////////////////////////

		if((paramsMap != null && paramsMap.size() > 0) ||
			(filesMap != null && filesMap.size() > 0)){
			// Form multipart with the paramsMap and filesMap
			RequestBody requestBody = buildMultipartBody(paramsMap, filesMap);

			// Attach RequestBody to the RequestBuilder
			reqBuilder = reqBuilder.post(requestBody);
		}

		////////////////////////////////////////////////////////////////////////

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

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
	public ResponseHttp putForm(//
		String reqUrl, //
		Map<String, String[]> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {

		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		if(paramMap != null){
			// Create the form with the paramMap
			RequestBody requestBody = buildFormBody(paramMap);

			// Attach RequestBody to the RequestBuilder
			reqBuilder.put(requestBody);
		}

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs PUT request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   params     [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp putJSON(
		String reqUrl, //
		Object params, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		try{
			String jsonString = ConvertJSON.fromObject(params);
			return putJSON_string(
					reqUrl,
					jsonString,
					cookiesMap,
					headersMap
			);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs PUT request : with json string as the parameter
	 *
	 * @param   Request URL to call
	 * @param   jsonString [can be null] JSON string to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp putJSON_string(
		String reqUrl, //
		String jsonString, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		////////////////////////////////////////////////////////////////////////

		// Ensure jsonString is not null
		jsonString = (jsonString != null) ? jsonString : "";

		RequestBody body = RequestBody.create(JSON, jsonString);
		reqBuilder = reqBuilder.put(body);

		////////////////////////////////////////////////////////////////////////

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Performs PUT request : using multipart
	 *
	 * @param   Request URL to call
	 * @param   paramsMap [can be null] Parameters to add to the request body,
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @param   filesMap   [can be null] Files to add to the request body
	 *
	 * @return  The ResponseHttp object
	 **/
	public ResponseHttp putMultipart(//
		String reqUrl, //
		Map<String, String[]> paramsMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap, //
		Map<String, File[]> filesMap //
	) {
		// Initialize the request builder with url and set up its headers
		Request.Builder reqBuilder = new Request.Builder().url(reqUrl);
		reqBuilder = setupRequestHeaders(reqBuilder, cookiesMap, headersMap);

		////////////////////////////////////////////////////////////////////////

		if((paramsMap != null && paramsMap.size() > 0) ||
				(filesMap != null && filesMap.size() > 0)){
			// Form multipart with the paramsMap and filesMap
			RequestBody requestBody = buildMultipartBody(paramsMap, filesMap);

			// Attach RequestBody to the RequestBuilder
			reqBuilder = reqBuilder.put(requestBody);
		}

		////////////////////////////////////////////////////////////////////////

		// Build the request, and make the call
		try{
			Response response = client.newCall( reqBuilder.build() ).execute();
			return new ResponseHttpImplementation(response);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	// ~GET~ / ~POST~ / PUT / DELETE

	////////////////////////////////////////////////////////////////////////
	//
	// Helper functions
	//
	////////////////////////////////////////////////////////////////////////

	private RequestBody buildFormBody(Map<String, String[]> paramMap) {
		// From the paramMap, create a RequestBody for attaching to the okhttp post method
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		for(String key : paramMap.keySet()){
			String[] values = paramMap.get(key);
			for(String value : values) {
				formBodyBuilder.add(key, value);
			}
		}
		return formBodyBuilder.build();
	}

	private RequestBody buildMultipartBody(
		Map<String, String[]> paramMap,
		Map<String, File[]> filesMap
	){
		MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
			.setType(MultipartBody.FORM);

		if(paramMap != null){
			for(String key : paramMap.keySet()){
				String[] values = paramMap.get(key);
				for(String value : values) {
					multipartBuilder.addFormDataPart(key, value);
				}
			}
		}

		if(filesMap != null){
			for(String key : filesMap.keySet()) {
				File[] files = filesMap.get(key);
				for(File file : files) {
					multipartBuilder.addFormDataPart(key, file.getName(),
						RequestBody.create(OCTET_STREAM, file));
				}
			}
		}

		return multipartBuilder.build();
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
	
	//------------------------------------------------
	//
	//  ResponseHttp implementation
	//
	//  PS : I know this is against my, 1 class per file rule,
	//       but the alternative is to do a sub namespace
	//
	//------------------------------------------------

	/**
	 * ResponseHttp implementation of OkHttp2
	 */
	protected class ResponseHttpImplementation implements ResponseHttp {
		
		/// OkHttp2 response object
		protected Response response = null;

		/**
		 * Constructor with response object
		 * 
		 * @param  inResponse object to setup
		 */
		protected ResponseHttpImplementation(Response inResponse) {
			response = inResponse;
		}


		/**
		 * Gets the response content
		 *
		 * @return InputStream of the response body
		 **/
		@Override
		public InputStream inputStream() {
			return response.body().byteStream();
		}

		/**
		 * Gets the response content as a string
		 *
		 * @return String of the response body
		 **/
		@Override
		public String toString() {
			try {
				return response.body().string();
			} catch (IOException io) {
				throw new RuntimeException(io);
			}

		}

		/**
		 * Gets the response code
		 * Refer to https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
		 *
		 * @return int status code of response
		 **/
		@Override
		public int statusCode() {
			return response.code();
		};

		/**
		 * Gets the header map.
		 *
		 * @return Map of the header's key value pairs
		 **/
		@Override
		public Map<String, String[]> headersMap() {
			// Get the header multimap
			Map<String, List<String>> headers = response.headers().toMultimap();
			// Convert List<String> values into String[]
			return MapValueConv.listToArray(headers, EmptyArray.STRING);
		};

		/**
		 * Gets the cookies map.
		 *
		 * @return Map of the cookies' key value pairs
		 **/
		@Override
		public Map<String, String[]> cookiesMap() {
			// @TODO: If redirection occurs, the cookies in the response headers may not
			//        correspond to the response.request().url(), hence it may have no
			//        cookies return back.
			//
			//        There may be a need to implement the tedious extraction of cookie
			//        headers from the response.headers() ourselves. (This is unconfirmed.)
			List<Cookie> cookies = Cookie.parseAll(response.request().url(), response.headers());

			// Extract the cookies from list and put them in Map<String, String[]> format
			Map<String, String[]> cookies_array = new HashMap<String, String[]>();
			for(Cookie cookie : cookies){
				cookies_array.put(cookie.name(), new String[] { cookie.value() });
			}

			// Return the extracted cookies
			return cookies_array;
		};
	}
}