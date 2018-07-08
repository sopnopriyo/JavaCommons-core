package picoded.core.web;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import picoded.core.web.RequestHttpClient;
import picoded.core.conv.ConvertJSON;
import picoded.core.conv.GUID;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test suite will handle the GET methods as well as the
 * ResponseHttp validation test units
 */
public class RequestHttpClient_GET_test{

	MockWebServer mockWebServer = null;
	RequestHttpClient requestHttpClient = null;

	@Before
	public void setup(){
		try{
			mockWebServer = new MockWebServer();

			// Start server at any available port in the system
			mockWebServer.start(0);

		} catch(IOException io) {
			throw new RuntimeException(io);
		}

		// Initialize the http client
		requestHttpClient = new RequestHttpClient();
	}

	@After
	public void destroy(){
		try{
			// Shut down the server. Instances cannot be reused.
			mockWebServer.shutdown();
		} catch(IOException io) {
			throw new RuntimeException(io);
		}
	}

	@Test
	public void initializeRequestHttpClient_test() {
		assertNotNull(requestHttpClient);
	}

	//------------------------------------------------
	//
	//  GET request test units
	//
	//------------------------------------------------

	/**
	 * This basic GET request test retrieves information from the server
	 */
	@Test
	public void basic_get_request() {
		// Simple hello world test
		// Add the body for the response
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.get( 
			mockWebServer.url("/").toString()
		);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

//		// Not able to test it as RecordedRequest does not return the url params correctly
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("first",  new String[]{ "random-value", "choose-value" });
//		params.put("second", "single-value");
//		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
//		responseHttp = requestHttpClient.get(
//				mockWebServer.url("/").toString(),
//				params,
//				null,
//				null);
//		assertEquals(responseHttp.statusCode(), 200);
//		assertEquals(responseHttp.toString(), "hello, world!");
//
//		// Check sent request's headers
//		RecordedRequest sentRequest = mockWebServer.takeRequest();
//		assertEquals("[\"abc\",\"def\",\"{'name':'ghi'}\"]",
//				sentRequest.getRequestUrl());
	}

	/**
	 * This test assert the headers sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_get_request() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpGet(
				mockWebServer.url("/").toString(),
				null,
				null,
				headers);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's headers
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> serverRequestHeaders = sentRequest.getHeaders().toMultimap();

		List<String> firstHeader = new ArrayList<String>();
		firstHeader.add("random-value");
		firstHeader.add("choose-value");
		assertEquals(firstHeader, serverRequestHeaders.get("first"));

		List<String> secondHeader = new ArrayList<String>();
		secondHeader.add("single-value");
		assertEquals(secondHeader, serverRequestHeaders.get("second"));
	}

	/**
	 * This test assert the cookies sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_get_request() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpGet(
				mockWebServer.url("/").toString(),
				null,
				cookiesMap,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's cookies
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> requestHeaders = sentRequest.getHeaders().toMultimap();

		List<String> cookies = new ArrayList<String>();
		cookies.add("cookie1=thiscookie; cookie1=anothercook; cookie2=myname");
		assertEquals(cookies, requestHeaders.get("cookie"));
	}

	//------------------------------------------------
	//
	//  ResponseHttp test units
	//
	//------------------------------------------------

	/**
	 * This test assert the headers is correctly
	 * received by ResponseHttp
	 *
	 */
	@Test
	public void headers_responseHttp() {
		// Adding response headers
		mockWebServer.enqueue(new MockResponse()
				.addHeader("first", "first value")
				.addHeader("first", "another value")
				.addHeader("second", "single value")
		);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpGet(
			mockWebServer.url("/").toString(),
			null,
			null,
			null);
		Map<String, String[]> headers = responseHttp.headersMap();
		assertNotNull(headers);
		assertEquals(headers.get("first"), new String[] { "first value", "another value" });
		assertEquals(headers.get("second"), new String[] { "single value" });
	}

	/**
	 * This test assert the cookies is correctly
	 * received by ResponseHttp
	 *
	 */
	@Test
	public void cookies_responseHttp() {
		// Adding testing response for cookies
		mockWebServer.enqueue(new MockResponse()
				.addHeader("Set-Cookie", "cookie1=thiscookie")
				.addHeader("Set-Cookie", "cookie2=myname")
		);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpGet(
			mockWebServer.url("/").toString(),
			null,
			null,
			null);
		Map<String, String[]> cookiesMap = responseHttp.cookiesMap();
		assertNotNull(cookiesMap);
		assertEquals(cookiesMap.get("cookie1"), new String[] { "thiscookie" });
		assertEquals(cookiesMap.get("cookie2"), new String[] { "myname" });
	}

	//------------------------------------------------
	//
	//  GET request test units for wrapper methods
	//
	//------------------------------------------------

	/**
	 * This basic GET request test retrieves information from the server
	 */
	@Test
	public void basic_get_request_wrapper() throws InterruptedException {
		// Simple hello world test
		// Add the body for the response
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.get(
				mockWebServer.url("/").toString(),
				null,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

//		// Not able to test it as RecordedRequest does not return the url params correctly
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("first",  new String[]{ "random-value", "choose-value" });
//		params.put("second", "single-value");
//		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
//		responseHttp = requestHttpClient.get(
//				mockWebServer.url("/").toString(),
//				params,
//				null,
//				null);
//		assertEquals(responseHttp.statusCode(), 200);
//		assertEquals(responseHttp.toString(), "hello, world!");
//
//		// Check sent request's headers
//		RecordedRequest sentRequest = mockWebServer.takeRequest();
//		assertEquals("[\"abc\",\"def\",\"{'name':'ghi'}\"]",
//				sentRequest.getRequestUrl());
	}

	/**
	 * This test assert the headers sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_get_request_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, Object> headers = new HashMap<>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.get(
				mockWebServer.url("/").toString(),
				null,
				null,
				headers);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's headers
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> serverRequestHeaders = sentRequest.getHeaders().toMultimap();

		List<String> firstHeader = new ArrayList<String>();
		firstHeader.add("random-value");
		firstHeader.add("choose-value");
		assertEquals(firstHeader, serverRequestHeaders.get("first"));

		List<String> secondHeader = new ArrayList<String>();
		secondHeader.add("single-value");
		assertEquals(secondHeader, serverRequestHeaders.get("second"));
	}

	/**
	 * This test assert the cookies sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_get_request_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, Object> cookiesMap = new HashMap<String, Object>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.get(
				mockWebServer.url("/").toString(),
				null,
				cookiesMap,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's cookies
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> requestHeaders = sentRequest.getHeaders().toMultimap();

		List<String> cookies = new ArrayList<String>();
		cookies.add("cookie1=thiscookie; cookie1=anothercook; cookie2=myname");
		assertEquals(cookies, requestHeaders.get("cookie"));
	}
}
