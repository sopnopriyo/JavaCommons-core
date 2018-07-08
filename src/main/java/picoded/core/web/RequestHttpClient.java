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
	public ResponseHttp post(String reqUrl, Map<String, Object> paramMap){
		return post(reqUrl, paramMap, null, null);
	}

	/**
	 * Wrapper method for POST form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @param   cookieMap  [can be null] Cookie map to send values
	 * @return ResponseHttp object
	 */
	public ResponseHttp post(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	){
		return httpPost(reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			convertMapObjectToStringArray(cookiesMap), //
			null //
		);
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
	public ResponseHttp post(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpPost(reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
		Map<String, Object> paramMap //
	){
		return postMultipart(reqUrl, paramMap, null, null, null);
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
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpPostMultipart(reqUrl, //
			convertMapObjectToStringArray(paramMap), // 
			filesMap, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
	public ResponseHttp postJSON(//
		String reqUrl, //
		Object params //
	) {
		return postJSON(reqUrl, params, null, null);
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
		Map<String, Object> cookiesMap //
	) {
		return postJSON(reqUrl, params, cookiesMap, null);
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
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return httpPostJSON( //
			reqUrl, //
			params, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
	public ResponseHttp put(String reqUrl, Map<String, Object> paramMap){
		return put(reqUrl, paramMap, null, null);
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
	public ResponseHttp put(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap //
	){
		return put(reqUrl, paramMap, cookiesMap, null);
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
	public ResponseHttp put(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpPutForm( //
			reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
		Map<String, Object> cookiesMap //
	){
		return putMultipart(reqUrl, paramMap, filesMap, cookiesMap, null);
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
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpPutMultipart( //
			reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			filesMap, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
	public ResponseHttp putJSON(//
		String reqUrl, //
		Object params //
	) {
		return putJSON(reqUrl, params, null, null);
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
	public ResponseHttp putJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap //
	) {
		return putJSON(reqUrl, params, cookiesMap, null);
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
	public ResponseHttp putJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return httpPutJSON( //
			reqUrl, //
			params, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
		return delete(reqUrl, null, null, null);
	}

	/**
	 * Wrapper method for DELETE form requests
	 *
	 * @param   Request URL to call
	 * @param   paramMap   [can be null] Parameters to add to the request
	 * @return ResponseHttp object
	 */
	public ResponseHttp delete(String reqUrl, Map<String, Object> paramMap){
		return delete(reqUrl, paramMap, null, null);
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
	public ResponseHttp delete(
		String reqUrl, //
		Map<String, Object> paramMap, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpDelete( //
			reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	){
		return httpDeleteMultipart( //
			reqUrl, //
			convertMapObjectToStringArray(paramMap), //
			filesMap, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
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
	public ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params //
	) {
		return deleteJSON(reqUrl, params, null, null);
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
	public ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap //
	) {
		return deleteJSON(reqUrl, params, cookiesMap, null);
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
	public ResponseHttp deleteJSON(//
		String reqUrl, //
		Object params, //
		Map<String, Object> cookiesMap, //
		Map<String, Object> headersMap //
	) {
		return httpDeleteJSON( //
			reqUrl, //
			params, //
			convertMapObjectToStringArray(cookiesMap), //
			convertMapObjectToStringArray(headersMap) //
		);
	}

}