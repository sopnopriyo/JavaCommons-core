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
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

/**
 * Test the default behaviour of ResponseHttp functionality
 */
public class ResponseHttp_test {

	public class TestDefaultResponseEmpty implements ResponseHttp {
		/**
		 * Gets the response content as a string
		 *
		 * @return String of the response body
		 **/
		public String toString() {
			return "";
		}
	}

	public class TestDefaultResponseMap implements ResponseHttp {
		/**
		 * Gets the response content as a string
		 *
		 * @return String of the response body
		 **/
		public String toString() {
			return "{}";
		}
	}


	/**
	 * Testing the empty string scenerio in the responseHttp
	 */
    @Test
    public final void defaultBehaviour_test() {
		ResponseHttp testDefaultResponse = new TestDefaultResponseEmpty();
		assertEquals("", testDefaultResponse.toString());
		assertNull(testDefaultResponse.inputStream());
		assertNull(testDefaultResponse.toMap());
		assertEquals(-1, testDefaultResponse.statusCode());
		assertNull(testDefaultResponse.headersMap());
		assertNull(testDefaultResponse.cookiesMap());
		assertEquals("", testDefaultResponse.method());
	}
	
	@Test
	public void emptyMap_test() {
		ResponseHttp testDefaultResponse = new TestDefaultResponseMap();
		assertEquals("{}", String.valueOf(testDefaultResponse.toMap()));
	}
}