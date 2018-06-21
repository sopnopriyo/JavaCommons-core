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

public class RequestHttpClient_test{

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
			mockWebServer.url("/").toString(),
			null,
			null,
			null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
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
	public void cookies_get_request() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
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
		ResponseHttp responseHttp = requestHttpClient.get(
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
		ResponseHttp responseHttp = requestHttpClient.get(
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
	//  POST request FORM test units
	//
	//------------------------------------------------

	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		Map<String, String[]> postBodyParams = new HashMap<String, String[]>();
		postBodyParams.put("first_value",  new String[]{ "single-value" });
		postBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postForm(
			mockWebServer.url("/").toString(),
			postBodyParams,
			null,
			null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("second_value=double-value&second_value=another-value&first_value=single-value",
			sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_post_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postForm(
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_post_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postForm(
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
	//  POST request JSON OBJECT test units
	//
	//------------------------------------------------

	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON() - Map<String, String[]>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_map_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		Map<String, String[]> postBodyParams = new HashMap<String, String[]>();
		postBodyParams.put("first_value",  new String[]{ "single-value" });
		postBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON(
				mockWebServer.url("/").toString(),
				postBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("{\"second_value\":[\"double-value\",\"another-value\"],\"first_value\":[\"single-value\"]}",
				sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON() - List<String>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_list_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		List<String> postBodyParams = new ArrayList<String>();
		postBodyParams.add("abc");
		postBodyParams.add("def");
		postBodyParams.add("{'name':'ghi'}");

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON(
				mockWebServer.url("/").toString(),
				postBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("[\"abc\",\"def\",\"{'name':'ghi'}\"]",
				sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_post_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON(
				mockWebServer.url("/").toString(),
				params,
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_post_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON(
				mockWebServer.url("/").toString(),
				params,
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
	//  POST request JSON STRING test units
	//
	//------------------------------------------------

	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_string_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body JSON string
		String json = "{'winCondition':'HIGH_SCORE',"
				+ "'name':'Bowling',"
				+ "'round':4,"
				+ "'lastSaved':1367702411696,"
				+ "'dateStarted':1367702378785,"
				+ "'players':["
				+ "{'name':'James','history':[10,8,6,7,8],'color':-13388315,'total':39},"
				+ "{'name':'Peter','history':[6,10,5,10,10],'color':-48060,'total':41}"
				+ "]}";

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON_string(
				mockWebServer.url("/").toString(),
				json,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals(json, sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_post_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON_string(
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_post_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postJSON_string(
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
	//  POST request MULTIPART test units
	//
	//------------------------------------------------

	/**
	 * This test assert that the params
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_multipart_params_only() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		Map<String, String[]> params = new HashMap<String, String[]>();
		String first = GUID.base64();
		String second = GUID.base64();
		String third = GUID.base64();
		params.put("first",  new String[] { first });
		params.put("second", new String[] { second, third });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postMultipart(
				mockWebServer.url("/").toString(),
				params,
				null,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();
		assertTrue(body.indexOf(first) >= 0);
		assertTrue(body.indexOf(second) >= 0);
		assertTrue(body.indexOf(third) >= 0);
	}


	/**
	 * This test assert that the filesMap
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_multipart_files_only() throws IOException, InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Generating random files with random content
		Map<String, File[]> filesMap= new HashMap<String, File[]>();
		int number = 3;
		File[] fileArray = new File[number];
		for(int i = 0; i < number; i++){
			File temp = File.createTempFile(GUID.base64(), ".tmp");
			String randomString = GUID.base64();
			FileOutputStream outputStream = new FileOutputStream(temp);
			byte[] strToBytes = randomString.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
			fileArray[i] = temp;
		}
		filesMap.put("files", fileArray);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postMultipart(
				mockWebServer.url("/").toString(),
				null,
				null,
				null,
				filesMap);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();

		for(File file : fileArray){
			// Assert that file name exists
			assertTrue(body.indexOf(file.getName()) >= 0);

			// Assert that the content of file exists
			String content = new String ( Files.readAllBytes( Paths.get(file.getAbsolutePath()) ) );
			assertTrue(body.indexOf(content) >= 0);
		}

	}

	/**
	 * This test assert that the params and fileMap
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_post_request_multipart_params_and_files() throws IOException, InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare params
		Map<String, String[]> params = new HashMap<String, String[]>();
		String first = GUID.base64();
		String second = GUID.base64();
		String third = GUID.base64();
		params.put("first",  new String[] { first });
		params.put("second", new String[] { second, third });

		// Generating random files with random content
		Map<String, File[]> filesMap= new HashMap<String, File[]>();
		int number = 3;
		File[] fileArray = new File[number];
		for(int i = 0; i < number; i++){
			File temp = File.createTempFile(GUID.base64(), ".tmp");
			String randomString = GUID.base64();
			FileOutputStream outputStream = new FileOutputStream(temp);
			byte[] strToBytes = randomString.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
			fileArray[i] = temp;
		}
		filesMap.put("files", fileArray);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postMultipart(
				mockWebServer.url("/").toString(),
				params,
				null,
				null,
				filesMap);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();

		assertTrue(body.indexOf(first) >= 0);
		assertTrue(body.indexOf(second) >= 0);
		assertTrue(body.indexOf(third) >= 0);

		for(File file : fileArray){
			// Assert that file name exists
			assertTrue(body.indexOf(file.getName()) >= 0);

			// Assert that the content of file exists
			String content = new String ( Files.readAllBytes( Paths.get(file.getAbsolutePath()) ) );
			assertTrue(body.indexOf(content) >= 0);
		}
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_post_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postMultipart(
				mockWebServer.url("/").toString(),
				null,
				null,
				headers,
				null);
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_post_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.postMultipart(
				mockWebServer.url("/").toString(),
				null,
				cookiesMap,
				null,
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
	//  PUT request FORM test units
	//
	//------------------------------------------------


	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		Map<String, String[]> postBodyParams = new HashMap<String, String[]>();
		postBodyParams.put("first_value",  new String[]{ "single-value" });
		postBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putForm(
				mockWebServer.url("/").toString(),
				postBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("second_value=double-value&second_value=another-value&first_value=single-value",
				sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_put_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putForm(
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_put_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putForm(
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
	//  PUT request JSON OBJECT test units
	//
	//------------------------------------------------
	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON() - Map<String, String[]>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_map_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		Map<String, String[]> postBodyParams = new HashMap<String, String[]>();
		postBodyParams.put("first_value",  new String[]{ "single-value" });
		postBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON(
				mockWebServer.url("/").toString(),
				postBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("{\"second_value\":[\"double-value\",\"another-value\"],\"first_value\":[\"single-value\"]}",
				sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON() - List<String>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_list_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body Params
		List<String> postBodyParams = new ArrayList<String>();
		postBodyParams.add("abc");
		postBodyParams.add("def");
		postBodyParams.add("{'name':'ghi'}");

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON(
				mockWebServer.url("/").toString(),
				postBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals("[\"abc\",\"def\",\"{'name':'ghi'}\"]",
				sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_put_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON(
				mockWebServer.url("/").toString(),
				params,
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_put_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON(
				mockWebServer.url("/").toString(),
				params,
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
	//  PUT request JSON STRING test units
	//
	//------------------------------------------------
	/**
	 * This test assert that the post request body
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_string_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare post body JSON string
		String json = "{'winCondition':'HIGH_SCORE',"
				+ "'name':'Bowling',"
				+ "'round':4,"
				+ "'lastSaved':1367702411696,"
				+ "'dateStarted':1367702378785,"
				+ "'players':["
				+ "{'name':'James','history':[10,8,6,7,8],'color':-13388315,'total':39},"
				+ "{'name':'Peter','history':[6,10,5,10,10],'color':-48060,'total':41}"
				+ "]}";

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON_string(
				mockWebServer.url("/").toString(),
				json,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		assertEquals(json, sentRequest.getUtf8Body());
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_put_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON_string(
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_put_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putJSON_string(
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
	//  PUT request MULTIPART test units
	//
	//------------------------------------------------


	/**
	 * This test assert that the params
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_multipart_params_only() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		Map<String, String[]> params = new HashMap<String, String[]>();
		String first = GUID.base64();
		String second = GUID.base64();
		String third = GUID.base64();
		params.put("first",  new String[] { first });
		params.put("second", new String[] { second, third });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putMultipart(
				mockWebServer.url("/").toString(),
				params,
				null,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();
		assertTrue(body.indexOf(first) >= 0);
		assertTrue(body.indexOf(second) >= 0);
		assertTrue(body.indexOf(third) >= 0);
	}


	/**
	 * This test assert that the filesMap
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_pt_request_multipart_files_only() throws IOException, InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Generating random files with random content
		Map<String, File[]> filesMap= new HashMap<String, File[]>();
		int number = 3;
		File[] fileArray = new File[number];
		for(int i = 0; i < number; i++){
			File temp = File.createTempFile(GUID.base64(), ".tmp");
			String randomString = GUID.base64();
			FileOutputStream outputStream = new FileOutputStream(temp);
			byte[] strToBytes = randomString.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
			fileArray[i] = temp;
		}
		filesMap.put("files", fileArray);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putMultipart(
				mockWebServer.url("/").toString(),
				null,
				null,
				null,
				filesMap);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();

		for(File file : fileArray){
			// Assert that file name exists
			assertTrue(body.indexOf(file.getName()) >= 0);

			// Assert that the content of file exists
			String content = new String ( Files.readAllBytes( Paths.get(file.getAbsolutePath()) ) );
			assertTrue(body.indexOf(content) >= 0);
		}

	}

	/**
	 * This test assert that the params and fileMap
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_put_request_multipart_params_and_files() throws IOException, InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare params
		Map<String, String[]> params = new HashMap<String, String[]>();
		String first = GUID.base64();
		String second = GUID.base64();
		String third = GUID.base64();
		params.put("first",  new String[] { first });
		params.put("second", new String[] { second, third });

		// Generating random files with random content
		Map<String, File[]> filesMap= new HashMap<String, File[]>();
		int number = 3;
		File[] fileArray = new File[number];
		for(int i = 0; i < number; i++){
			File temp = File.createTempFile(GUID.base64(), ".tmp");
			String randomString = GUID.base64();
			FileOutputStream outputStream = new FileOutputStream(temp);
			byte[] strToBytes = randomString.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
			fileArray[i] = temp;
		}
		filesMap.put("files", fileArray);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putMultipart(
				mockWebServer.url("/").toString(),
				params,
				null,
				null,
				filesMap);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();
		String body = sentRequest.getUtf8Body();

		assertTrue(body.indexOf(first) >= 0);
		assertTrue(body.indexOf(second) >= 0);
		assertTrue(body.indexOf(third) >= 0);

		for(File file : fileArray){
			// Assert that file name exists
			assertTrue(body.indexOf(file.getName()) >= 0);

			// Assert that the content of file exists
			String content = new String ( Files.readAllBytes( Paths.get(file.getAbsolutePath()) ) );
			assertTrue(body.indexOf(content) >= 0);
		}
	}

	/**
	 * This test assert that the headers
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_put_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putMultipart(
				mockWebServer.url("/").toString(),
				null,
				null,
				headers,
				null);
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
	 * This test assert that the cookies
	 * is correctly sent via POST to the server
	 * using postMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_put_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.putMultipart(
				mockWebServer.url("/").toString(),
				null,
				cookiesMap,
				null,
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
