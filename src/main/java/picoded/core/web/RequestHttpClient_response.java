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
 * ResponseHttp implementation of OkHttp2
 */
class RequestHttpClient_response implements ResponseHttp {
	
	/// OkHttp2 response object
	protected Response response = null;
	
	/**
	 * Constructor with response object
	 * 
	 * @param  inResponse object to setup
	 */
	protected RequestHttpClient_response(Response inResponse) {
		response = inResponse;
	}
	
	/** Cached response body object */
	protected ResponseBody _body = null;	

	/** @return the responseBody object */
	protected ResponseBody getResponseBody() {
		if( _body != null ) {
			return _body;
		}
		_body = response.body();
		return _body;
	}

	/// Flag indicating close operation was performed
	protected boolean _close = false;

	/** Closes the response body */
	public void close() throws Exception {
		// Skip if previously closed
		if(_close) {
			return;
		}
		_close = true;
		getResponseBody().close();
	}

	/**
	 * Gets the response content, note if using this .close() must be called manually
	 *
	 * @return InputStream of the response body
	 **/
	@Override
	public InputStream inputStream() {
		return getResponseBody().byteStream();
	}
	
	/** Cached response string */
	protected String _responseString = null;	

	/**
	 * Gets the response content as a string, and closes the connection
	 *
	 * @return String of the response body
	 **/
	@Override
	public String toString() {
		try {
			if( _responseString != null ) {
				return _responseString;
			}
			_responseString = getResponseBody().string();
			try {
				close();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			return _responseString;
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
		for (Cookie cookie : cookies) {
			cookies_array.put(cookie.name(), new String[] { cookie.value() });
		}
		
		// Return the extracted cookies
		return cookies_array;
	};
	
	/**
	 * Gets the method of the request
	 */
	@Override
	public String method() {
		return response.request().method();
	}
}