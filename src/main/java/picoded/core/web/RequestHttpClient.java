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
 *		/// for subsequent quick reuse
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
public final class RequestHttpClient extends RequestHttpClient_base {

	//------------------------------------------------
	//
	//  Constructor
	//
	//------------------------------------------------

	/**
	 * Setup the RequestHttpClient with default configuration settings
	 */
	public RequestHttpClient() {
		super();
	}

	/**
	 * Setup the RequestHttpClient with custom configuration settings
	 * 
	 * @param  config map to be used, can be null
	 */
	public RequestHttpClient(Map<String,Object> inConfig) {
		super(inConfig);
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
	public ResponseHttp get(
		String reqUrl //
	){
		return get(reqUrl, null, null, null);
	}

	/**
	 * Wrapper method for GET request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public ResponseHttp get(
		String reqUrl, //
		Map<String, Object> paramMap //
	){
		return get(reqUrl, paramMap, null, null);
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
	public ResponseHttp get(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	){
		return get(reqUrl, paramMap, cookiesMap, null);
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
	public ResponseHttp get(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpGet( //
			reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
	}

	//------------------------------------------------
	//
	//  Wrappers for POST Requests
	//
	//------------------------------------------------

	/**
	 * Wrapper method for POST form request
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public ResponseHttp postForm(String reqUrl, Map<String, Object> paramMap){
		return postForm(reqUrl, paramMap, null, null);
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
	public ResponseHttp postForm(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpPostForm(reqUrl, reformedParamMap, cookiesMap, headersMap);
	}

	/**
	 * Wrapper method for POST multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public ResponseHttp postMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	){
		return postMultipart(reqUrl, paramMap, filesMap, null, null);
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
	public ResponseHttp postMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpPostMultipart(reqUrl, reformedParamMap, filesMap, cookiesMap, headersMap);
	}

	//------------------------------------------------
	//
	//  Wrappers for PUT Requests
	//
	//------------------------------------------------

	/**
	 * Wrapper method for PUT form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public ResponseHttp putForm(String reqUrl, Map<String, Object> paramMap){
		return putForm(reqUrl, paramMap, null, null);
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
	public ResponseHttp putForm(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpPutForm(reqUrl, reformedParamMap, cookiesMap, headersMap);
	}


	/**
	 * Wrapper method for PUT multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public ResponseHttp putMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	){
		return putMultipart(reqUrl, paramMap, filesMap, null, null);
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
	public ResponseHttp putMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpPutMultipart(reqUrl, reformedParamMap, filesMap, cookiesMap, headersMap);
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
	public ResponseHttp delete(String reqUrl){
		return deleteForm(reqUrl, null, null, null);
	}

	/**
	 * Wrapper method for DELETE form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public ResponseHttp deleteForm(String reqUrl, Map<String, Object> paramMap){
		return deleteForm(reqUrl, paramMap, null, null);
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
	public ResponseHttp deleteForm(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpDeleteForm(reqUrl, reformedParamMap, cookiesMap, headersMap);
	}

	/**
	 * Wrapper method for DELETE multipart requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   filesMap   [can be null] Files to add to the request body
	 * @return ResponseHttp object
	 */
	public ResponseHttp deleteMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap //
	){
		return deleteMultipart(reqUrl, paramMap, filesMap, null, null);
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
	public ResponseHttp deleteMultipart(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, File[]> filesMap, //
		Map<String, String[]> cookiesMap, //
		Map<String, String[]> headersMap //
	){
		Map<String, String[]> reformedParamMap = convertMapObjectToStringArray(paramMap);
		return httpDeleteMultipart(reqUrl, reformedParamMap, filesMap, cookiesMap, headersMap);
	}

}