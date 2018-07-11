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