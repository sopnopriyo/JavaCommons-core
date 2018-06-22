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
 * This test suite will handle the DELETE methods
 */
public class RequestHttpClient_DELETE_test{

	MockWebServer mockWebServer = null;
	RequestHttpClient requestHttpClient = null;
	String httpbinURL = "https://httpbin.org/delete";

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

	//------------------------------------------------
	//
	//  DELETE request FORM test units
	//
	//------------------------------------------------

	/**
	 * This test assert that the delete request body
	 * is correctly sent via DELETE to the server
	 * using httpDeleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare delete body Params
		Map<String, String[]> deleteBodyParams = new HashMap<String, String[]>();
		deleteBodyParams.put("first_value",  new String[]{ "single-value" });
		deleteBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteForm(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteForm(
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_form() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteForm(
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
	//  DELETE request JSON OBJECT test units
	//
	//------------------------------------------------
	/**
	 * This test assert that the delete request body
	 * is correctly sent via DELETE to the server
	 * using deleteJSON() - Map<String, String[]>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_map_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare delete body Params
		Map<String, String[]> deleteBodyParams = new HashMap<String, String[]>();
		deleteBodyParams.put("first_value",  new String[]{ "single-value" });
		deleteBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
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
	 * This test assert that the delete request body
	 * is correctly sent via DELETE to the server
	 * using deleteJSON() - List<String>
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_list_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare delete body Params
		List<String> deleteBodyParams = new ArrayList<String>();
		deleteBodyParams.add("abc");
		deleteBodyParams.add("def");
		deleteBodyParams.add("{'name':'ghi'}");

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
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
	 * is correctly sent via DELETE to the server
	 * using deleteJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON(
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
	 * is correctly sent via DELETE to the server
	 * using deleteJSON()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Empty paramBody
		Map<String, String[]> params = new HashMap<String, String[]>();

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON(
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
	//  DELETE request JSON STRING test units
	//
	//------------------------------------------------
	/**
	 * This test assert that the delete request body
	 * is correctly sent via DELETE to the server
	 * using deleteJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_string_json() throws InterruptedException {

		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare delete body JSON string
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
		ResponseHttp responseHttp = requestHttpClient.deleteJSON_string(
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
	 * is correctly sent via DELETE to the server
	 * using deleteJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON_string(
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
	 * is correctly sent via DELETE to the server
	 * using deleteJSON_string()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_string_json() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteJSON_string(
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
	//  DELETE request MULTIPART test units
	//
	//------------------------------------------------


	/**
	 * This test assert that the params
	 * is correctly sent via DELETE to the server
	 * using httpDeleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_params_only() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		Map<String, String[]> params = new HashMap<String, String[]>();
		String first = GUID.base64();
		String second = GUID.base64();
		String third = GUID.base64();
		params.put("first",  new String[] { first });
		params.put("second", new String[] { second, third });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteMultipart(
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_files_only() throws IOException, InterruptedException {
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
		ResponseHttp responseHttp = requestHttpClient.httpDeleteMultipart(
				mockWebServer.url("/").toString(),
				null,
				filesMap,
				null,
				null);
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_params_and_files() throws IOException, InterruptedException {
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
		ResponseHttp responseHttp = requestHttpClient.httpDeleteMultipart(
				mockWebServer.url("/").toString(),
				params,
				filesMap,
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteMultipart(
				mockWebServer.url("/").toString(),
				null,
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
	 * is correctly sent via DELETE to the server
	 * using httpDeleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_multipart() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteMultipart(
				mockWebServer.url("/").toString(),
				null,
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

	@Test
	public void http_bin_delete_method(){
		// Prepare delete body Params
		Map<String, String[]> deleteBodyParams = new HashMap<String, String[]>();
		deleteBodyParams.put("first_value",  new String[]{ "single-value" });
		deleteBodyParams.put("second_value", new String[]{ "double-value", "another-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.httpDeleteForm(
				httpbinURL,
				deleteBodyParams,
				null,
				null);
		assertEquals("DELETE", responseHttp.method());
		assertEquals(responseHttp.statusCode(), 200);

		// Retrieve mockResponse from server and assert the results
		responseHttp = requestHttpClient.deleteJSON(
				httpbinURL,
				deleteBodyParams,
				null,
				null);
		assertEquals("DELETE", responseHttp.method());
		assertEquals(responseHttp.statusCode(), 200);

		// Retrieve mockResponse from server and assert the results
		responseHttp = requestHttpClient.deleteJSON_string(
				httpbinURL,
				"",
				null,
				null);
		assertEquals("DELETE", responseHttp.method());
		assertEquals(responseHttp.statusCode(), 200);

		// Retrieve mockResponse from server and assert the results
		// @TODO: If params and fileMap is null, it will become a GET method!
		responseHttp = requestHttpClient.httpDeleteMultipart(
				httpbinURL,
				deleteBodyParams,
				null,
				null,
				null);
		assertEquals("DELETE", responseHttp.method());
		assertEquals(responseHttp.statusCode(), 200);
	}




	//------------------------------------------------
	//
	//  DELETE request FORM test units for wrapper methods
	//
	//------------------------------------------------

	/**
	 * This test assert that the delete request body
	 * is correctly sent via DELETE to the server
	 * using deleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_form_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare delete body Params
		Map<String, Object> deleteBodyParams = new HashMap<String, Object>();
		deleteBodyParams.put("first_value",  "single-value" );
		deleteBodyParams.put("second_value", new String[]{ "double-value", "another-value" });
		deleteBodyParams.put("third_value",  999);
		deleteBodyParams.put("fourth_value", null);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteForm(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();

		assertEquals("second_value=double-value&second_value=another-value&fourth_value=null&first_value=single-value&third_value=999",
				sentRequest.getUtf8Body());

	}

	/**
	 * This test assert that the headers
	 * is correctly sent via DELETE to the server
	 * using deleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_form_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteForm(
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
	 * is correctly sent via DELETE to the server
	 * using deleteForm()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_form_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteForm(
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
	//  DELETE request MULTIPART test units for wrapper methods
	//
	//------------------------------------------------


	/**
	 * This test assert that the params
	 * is correctly sent via DELETE to the server
	 * using deleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_params_only_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		String first = GUID.base64();
		String second = GUID.base64();
		int third = 827382;

		// Prepare delete body Params
		Map<String, Object> deleteBodyParams = new HashMap<String, Object>();
		deleteBodyParams.put("first_value",  first );
		deleteBodyParams.put("second_value", second);
		deleteBodyParams.put("third_value",  third);
		deleteBodyParams.put("fourth_value", null);

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteMultipart(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
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
		assertTrue(body.indexOf(Integer.toString(third)) >= 0);
		assertTrue(body.indexOf("null") >= 0);
	}


	/**
	 * This test assert that the filesMap
	 * is correctly sent via DELETE to the server
	 * using deleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_files_only_wrapper() throws IOException, InterruptedException {
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
		ResponseHttp responseHttp = requestHttpClient.deleteMultipart(
				mockWebServer.url("/").toString(),
				null,
				filesMap,
				null,
				null);
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
	 * is correctly sent via DELETE to the server
	 * using deleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void basic_delete_request_multipart_params_and_files_wrapper() throws IOException, InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare params
		Map<String, String[]> params = new HashMap<String, String[]>();

		String first = GUID.base64();
		String second = GUID.base64();
		int third = 827382;

		// Prepare delete body Params
		Map<String, Object> deleteBodyParams = new HashMap<String, Object>();
		deleteBodyParams.put("first_value",  first );
		deleteBodyParams.put("second_value", second);
		deleteBodyParams.put("third_value",  third);

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
		ResponseHttp responseHttp = requestHttpClient.deleteMultipart(
				mockWebServer.url("/").toString(),
				deleteBodyParams,
				filesMap,
				null,
				null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");

		// Check sent request's body
		RecordedRequest sentRequest = mockWebServer.takeRequest();

		String body = sentRequest.getUtf8Body();

		assertTrue(body.indexOf(first) >= 0);
		assertTrue(body.indexOf(second) >= 0);
		assertTrue(body.indexOf(Integer.toString(third)) >= 0);

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
	 * is correctly sent via DELETE to the server
	 * using deleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void headers_delete_request_multipart_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare headers
		Map<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("first",  new String[]{ "random-value", "choose-value" });
		headers.put("second", new String[]{ "single-value" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteMultipart(
				mockWebServer.url("/").toString(),
				null,
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
	 * is correctly sent via DELETE to the server
	 * using deleteMultipart()
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void cookies_delete_request_multipart_wrapper() throws InterruptedException {
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

		// Prepare cookie map
		Map<String, String[]> cookiesMap = new HashMap<String, String[]>();
		cookiesMap.put("cookie1", new String[]{ "thiscookie", "anothercook" });
		cookiesMap.put("cookie2", new String[]{ "myname" });

		// Retrieve mockResponse from server and assert the results
		ResponseHttp responseHttp = requestHttpClient.deleteMultipart(
				mockWebServer.url("/").toString(),
				null,
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