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

import picoded.core.web.RequestHttp;
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
 * This test cases will cover all the funcitonality of RequestHttp
 * The HTTP request methods to be tested are : GET, POST, PUT, DELETE
 */
public class RequestHttp_test {

	MockWebServer mockWebServer = null;
	RequestHttp requestHttp = null;

	@Before
	public void setup() {
		try {
			mockWebServer = new MockWebServer();

			// Start server at any available port in the system
			mockWebServer.start(0);

		} catch (IOException io) {
			throw new RuntimeException(io);
		}

		// Initialize the http client
		requestHttp = new RequestHttp();
	}

	@After
	public void destroy() {
		try {
			// Shut down the server. Instances cannot be reused.
			mockWebServer.shutdown();
		} catch (IOException io) {
			throw new RuntimeException(io);
		}
	}

	@Test
	public void initializeRequestHttp_test() {
		assertNotNull(requestHttp);
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
	public void getRequestBasic_test() {
		// Simple hello world test
		// Add the body for the response
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttp.get(mockWebServer.url("/").toString());
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
	}

	/**
	 * This test assert the request params sent via GET request
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void getRequestWithParams_test() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		
		// Prepare headers
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("first", "hello");
		requestParams.put("second", "world");
		
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttp.get(mockWebServer.url("/").toString(), requestParams);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
		
		// Check sent request's params
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("/?first=hello&second=world", sentRequest.getPath());
	}

	/**
	 * This test assert the cookies sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void getRequestWithCookies_test() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		
		// Prepare cookie map
		Map<String, Object> cookiesMap = new HashMap<String, Object>();
		cookiesMap.put("cookie1", "thiscookie");
		cookiesMap.put("cookie2", "myname");
		
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttp.get(mockWebServer.url("/").toString(),
			null, cookiesMap, null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
		
		// Check sent request's cookies
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> requestHeaders = sentRequest.getHeaders().toMultimap();
		
		List<String> cookies = new ArrayList<String>();
		cookies.add("cookie1=thiscookie; cookie2=myname");
		assertEquals(cookies, requestHeaders.get("cookie"));
	}


	/**
	 * This test assert the headers sent via GET
	 * was received correctly on the server side
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void getRequestWithHeaders_test() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		
		// Prepare headers
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("first", "random-value");
		headers.put("second", "single-value");
		
		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttp.get(mockWebServer.url("/").toString(),
			null, null, headers);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
		
		// Check sent request's headers
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		Map<String, List<String>> serverRequestHeaders = sentRequest.getHeaders().toMultimap();
		
		List<String> firstHeader = new ArrayList<String>();
		firstHeader.add("random-value");
		assertEquals(firstHeader, serverRequestHeaders.get("first"));
		
		List<String> secondHeader = new ArrayList<String>();
		secondHeader.add("single-value");
		assertEquals(secondHeader, serverRequestHeaders.get("second"));
	}
}
