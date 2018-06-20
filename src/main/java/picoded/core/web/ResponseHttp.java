package picoded.core.web;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

import picoded.core.conv.ConvertJSON;
import picoded.core.struct.GenericConvertMap;
import picoded.core.struct.ProxyGenericConvertMap;

public interface ResponseHttp {
	
	//	///////////////////////////////////////////////////
	//	// Async Http Request wait handling
	//	// (implement if needed)
	//	///////////////////////////////////////////////////
	//
	//	/**
	//	 * Wait for completed header request, called automatically
	//	 * when getting InputStream / cookies / headers
	//	 **/
	//	public default void waitForCompletedHeaders() {
	//	};
	//
	//	/**
	//	 * Wait for completed request, called automatically
	//	 * when using toString / toMap
	//	 **/
	//	public default void waitForCompletedRequest() {
	//	};
	
	///////////////////////////////////////////////////
	// Response handling
	///////////////////////////////////////////////////
	
	/**
	 * Gets the response content
	 *
	 * @return InputStream of the response body
	 **/
	public default InputStream inputStream() {
		return null;
	};
	
	/**
	 * Gets the response content as a string
	 *
	 * @return String of the response body
	 **/
	public String toString();
	
	/**
	 * Converts the result string into a map, via JSON's
	 **/
	public default GenericConvertMap<String, Object> toMap() {
		// waitForCompletedRequest();
		
		String r = toString();
		if (r == null || r.length() <= 1) {
			return null;
		}
		
		Map<String, Object> rMap = ConvertJSON.toMap(r);
		if (rMap == null) {
			return null;
		} else {
			return ProxyGenericConvertMap.ensure(rMap);
		}
	};

	/**
	 * Gets the response code
	 * Refer to https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
	 *
	 * @return int status code of response
	 **/
	public default int statusCode() {
		return -1;
	};

	/**
	 * Gets the header map.
	 *
	 * @return Map of the header's key value pairs
	 **/
	public default Map<String, String[]> headersMap() {
		return null;
	};

	/**
	 * Gets the cookies map.
	 *
	 * @return Map of the cookies' key value pairs
	 **/
	public default Map<String, String[]> cookiesMap() {
		return null;
	};

}
