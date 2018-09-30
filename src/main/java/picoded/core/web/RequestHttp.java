package picoded.core.web;

import picoded.core.common.HttpRequestType;
import picoded.core.conv.GenericConvert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import picoded.core.common.EmptyArray;

/**
 * Sometimes you just want to do a simple HTTP Request and response
 * that JUST WORKS. Without needing to handle like a dozen over import types,
 * or complex setup (staring at you apache).
 *
 * Aka: KISS (for) the user programmer (using) this class (api)
 *
 * KISS: Keep It Simple Stupid.
 *
 * @TODO : A Map<String,String>, String[] String[], Map<String,Object> request version
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~{.java}
 *
 * // You can use your relevent URL string that you want
 * String requestURL = "http://localhost:8080"
 *
 * // Get request in its simplest form
 * String getResult = RequestHttp.get( requestURL ).toString()
 *
 * // Or with parameters
 * Map<String,Object> requestParams = new HashMap<String,Object>();
 * requestParams.put("test","one");
 *
 * // Get request with parameters
 * getResult = RequestHttp.get( requestURL, requestParams ).toString();
 *
 * // That can also be made as a POST request
 * getResult = RequestHttp.post( requestURL, requestParams ).toString();
 *
 * // Or other less commonly used types
 * getResult = RequestHttp.put( requestURL, requestParams ).toString();
 * getResult = RequestHttp.delete( requestURL, requestParams ).toString();
 *
 * //
 * // You may also want to get additional possible values
 * //
 * HttpResponse response = RequestHttp.get( requestURL );
 *
 * // Such as HTTP Code
 * response.statusCode();
 *
 * // Its headers and cookies map
 * response.headersMap();
 * response.cookiesMap();
 *
 * // Oh similarly you can use that in your request too, with Map<String, String[]>
 * RequestHttp.get( requestURL, requestParams, cookies, headers );
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 **/
public class RequestHttp {
	
	//--------------------------------------------------------
	// Static client library support
	//--------------------------------------------------------
	
	// Local static client variable
	private static volatile RequestHttpClient clientObj = null;
	
	/**
	 * Get and return the static client object
	 */
	public static RequestHttpClient client() {
		// Thread safe get
		if (clientObj != null) {
			return clientObj;
		}
		// Client object not yet initialized, try to get one
		// with a syncronized lock, initializing if needed
		synchronized (RequestHttp.class) {
			// This is to resolve race conditions,
			// where a seperate thread setup the client obj
			if (clientObj != null) {
				return clientObj;
			}
			
			// Initialize the client object
			clientObj = new RequestHttpClient();
			return clientObj;
		}
	}
	
	//------------------------------------------------
	//
	//  Wrappers for GET Requests
	//
	//------------------------------------------------
	
	/**
	 * Wrapper method for GET request
	 *
	 * @param   Request URL to call
	 * @return ResponseHttp object
	 */
	public static ResponseHttp get(String reqUrl) {
		return client().get(reqUrl);
	}
	
	/**
	 * Wrapper method for GET request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public static ResponseHttp get(String reqUrl, //
		Map<String, Object> paramMap //
	) {
		return client().get(reqUrl, paramMap);
	}
	
	/**
	 * Wrapper method for GET request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp get(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().get(reqUrl, paramMap, cookiesMap);
	}
	
	/**
	 * Wrapper method for GET request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp get(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().get(reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  Wrappers for POST form / multipart support
	//
	//------------------------------------------------
	
	/**
	 * Wrapper method for POST form request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public static ResponseHttp post(String reqUrl, Map<String, Object> paramMap) {
		return client().post(reqUrl, paramMap);
	}
	
	/**
	 * Wrapper method for POST form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp post(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().post(reqUrl, paramMap, cookiesMap);
	}
	
	/**
	 * Wrapper method for POST form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp post(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().post(reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Wrapper method for POST multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public static ResponseHttp postMultipart(String reqUrl, //
		Map<String, Object> paramMap //
	) {
		return client().postMultipart(reqUrl, paramMap);
	}
	
	/**
	 * Wrapper method for POST multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public static ResponseHttp postMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	) {
		return client().postMultipart(reqUrl, paramMap, filesMap);
	}
	
	/**
	 * Wrapper method for POST multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp postMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().postMultipart(reqUrl, paramMap, filesMap, cookiesMap, null);
	}
	
	/**
	 * Wrapper method for POST multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp postMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().postMultipart(reqUrl, paramMap, filesMap, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  Post JSON
	//
	//------------------------------------------------
	
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
	public static ResponseHttp postJSON(//
		String reqUrl, //
		Object params //
	) {
		return client().postJSON(reqUrl, params);
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
	public static ResponseHttp postJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap //
	) {
		return client().postJSON(reqUrl, params, cookiesMap);
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
	public static ResponseHttp postJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().postJSON(reqUrl, params, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  Wrappers for PUT form / multipart support
	//
	//------------------------------------------------
	
	/**
	 * Wrapper method for PUT form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public static ResponseHttp put(String reqUrl, Map<String, Object> paramMap) {
		return client().put(reqUrl, paramMap);
	}
	
	/**
	 * Wrapper method for PUT form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp put(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().put(reqUrl, paramMap, cookiesMap);
	}
	
	/**
	 * Wrapper method for PUT form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp put(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().put(reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Wrapper method for PUT multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public static ResponseHttp putMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	) {
		return client().putMultipart(reqUrl, paramMap, filesMap);
	}
	
	/**
	 * Wrapper method for PUT multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp putMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().putMultipart(reqUrl, paramMap, filesMap, cookiesMap);
	}
	
	/**
	 * Wrapper method for PUT multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp putMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().putMultipart(reqUrl, paramMap, filesMap, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  PUT JSON
	//
	//------------------------------------------------
	
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
	public static ResponseHttp putJSON(//
		String reqUrl, //
		Object params //
	) {
		return client().putJSON(reqUrl, params);
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
	public static ResponseHttp putJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap //
	) {
		return client().putJSON(reqUrl, params, cookiesMap);
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
	public static ResponseHttp putJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().putJSON(reqUrl, params, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  Wrappers for DELETE Requests
	//
	//------------------------------------------------
	
	/**
	 * Wrapper method for DELETE form requests
	 *
	 * @param   Request URL to call
	 * @return ResponseHttp object
	 */
	public static ResponseHttp delete(String reqUrl) {
		return client().delete(reqUrl);
	}
	
	/**
	 * Wrapper method for DELETE form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public static ResponseHttp delete(String reqUrl, Map<String, Object> paramMap) {
		return client().delete(reqUrl, paramMap);
	}
	
	/**
	 * Wrapper method for DELETE form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp delete(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().delete(reqUrl, paramMap, cookiesMap, headersMap);
	}
	
	/**
	 * Wrapper method for DELETE multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public static ResponseHttp deleteMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	) {
		return client().deleteMultipart(reqUrl, paramMap, filesMap);
	}
	
	/**
	 * Wrapper method for DELETE multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp deleteMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap //
	) {
		return client().deleteMultipart(reqUrl, paramMap, filesMap, cookiesMap, null);
	}
	
	/**
	 * Wrapper method for DELETE multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 * @return ResponseHttp object
	 */
	public static ResponseHttp deleteMultipart(String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().deleteMultipart(reqUrl, paramMap, filesMap, cookiesMap, headersMap);
	}
	
	//------------------------------------------------
	//
	//  DELETE JSON
	//
	//------------------------------------------------
	
	/**
	 * Performs DELETE request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   params     [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public static ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params //
	) {
		return client().deleteJSON(reqUrl, params);
	}
	
	/**
	 * Performs DELETE request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   params     [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public static ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap //
	) {
		return client().deleteJSON(reqUrl, params, cookiesMap, null);
	}
	
	/**
	 * Performs DELETE request : with json parameters as Map<String, String[]>
	 *
	 * @param   Request URL to call
	 * @param   params     [can be null] JSON valid Java objects to add to the request body
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @param   headersMap [can be null] Headers map to send values
	 *
	 * @return  The ResponseHttp object
	 **/
	public static ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return client().deleteJSON(reqUrl, params, cookiesMap, headersMap);
	}
	
}
