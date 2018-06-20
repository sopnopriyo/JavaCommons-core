package picoded.core.web;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import picoded.core.web.RequestHttpClient;
import picoded.core.conv.ConvertJSON;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;

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
			mockWebServer.start(0);
		} catch(IOException io){
			throw new RuntimeException(io);
		}

		requestHttpClient = new RequestHttpClient();
	}

	@After
	public void destroy(){

	}

	@Test
	public void initializeRequestHttpClient_test() {
		assertNotNull(requestHttpClient);
	}

	@Test
	public void basic_GETRequest() {
		// Simple hello world test
		mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
		ResponseHttp responseHttp = requestHttpClient.get(mockWebServer.url("/").toString(), null, null, null);
		assertEquals(responseHttp.statusCode(), 200);
		assertEquals(responseHttp.toString(), "hello, world!");
	}

	@Test
	public void headers_GETRequest() {
		mockWebServer.enqueue(new MockResponse()
				.addHeader("first", "first value")
				.addHeader("first", "another value")
				.addHeader("second", "single value")
		);

		ResponseHttp responseHttp = requestHttpClient.get(mockWebServer.url("/").toString(), null, null, null);
		Map<String, String[]> headers = responseHttp.headersMap();
		assertNotNull(headers);
		assertEquals(headers.get("first"), new String[] { "first value", "another value" });
		assertEquals(headers.get("second"), new String[] { "single value" });
	}

	@Test
	public void cookies_GETRequest() {
		mockWebServer.enqueue(new MockResponse()
				.addHeader("Set-Cookie", "cookie1=thiscookie")
				.addHeader("Set-Cookie", "cookie2=myname")
		);

		ResponseHttp responseHttp = requestHttpClient.get(mockWebServer.url("/").toString(), null, null, null);
		Map<String, String[]> cookiesMap = responseHttp.cookiesMap();
		assertNotNull(cookiesMap);
		assertEquals(cookiesMap.get("cookie1"), new String[] { "thiscookie" });
		assertEquals(cookiesMap.get("cookie2"), new String[] { "myname" });
	}

}
