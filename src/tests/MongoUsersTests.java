package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import java.net.URLEncoder;

public class MongoUsersTests {
	@Test
	public void testLogin() {
		try {
			String version = "v1+json";
			String endpoint =  "login";
			System.out.println("Endpoint: /" + endpoint);
			String request = "{\"userid\":\"johngrey\",\"password\":\"johngrey123!\"}";
			System.out.println("Resuest: " + request);
			System.out.println(HttpRequest.httpPost(endpoint,request,version));
			request = "{\"userid\":\"johngry\",\"password\":\"johngrey123!\"}";
			System.out.println("Resuest: " + request);
			System.out.println(HttpRequest.httpPost(endpoint,request,version));
			request = "{\"userid\":\"johngrey\"}";
			System.out.println("Resuest: " + request);
			System.out.println(HttpRequest.httpPost(endpoint,request,version));
			request = "{\"user\":\"johngrey\",\"password\":\"johngrey123!\"}";
			System.out.println("Resuest: " + request);
			System.out.println(HttpRequest.httpPost(endpoint,request,version));
		}
		catch (Exception e) {
				fail(e.getMessage() );
		}
	}
	@Test
	public void testUsers() {
		String endPoint = null;
		try {
			endPoint = "users?filter=" + URLEncoder.encode("{\"firstname\":\"amy\",\"lastname\":\"green\"}","UTF-8");
			System.out.println("Endpoint: /" + endPoint);
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"firstname\":\"amy\"}","UTF-8") +	"&groupby=lastname";
			System.out.println("Endpoint: /" + endPoint);
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"firstname\":\"amy\"}","UTF-8") + 
					"&groupby=address.zipcode&page_size=2&page_number=0";
			System.out.println("Endpoint: /" + endPoint);
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"lastname\":\"green\"}","UTF-8") + 
					"&groupby=profession&page_size=2&page_number=0";
			System.out.println("Endpoint: /" + endPoint);			
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"lastname\":\"green\"}","UTF-8") + 
					"&groupby=profession&page_size=2&page_number=1";
			System.out.println("Endpoint: /" + endPoint);
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
		}
		catch (Exception e) {
			String testEndPoint = (endPoint == null) ? "" : " endpoint - " + endPoint;
			fail("testUsers:" + testEndPoint + ", exception - " + e.getMessage() );
		}
	}
	@Test
	public void testStatus() {
		try {
			System.out.println("Endpoint: /status");
			System.out.println(HttpRequest.httpGet("status","v1"));
		}
		catch (Exception e) {
				fail(e.getMessage() );
		}
	}
	@Test
	public void testFiles() {
		try {
			System.out.println("Endpoint: /files/c:" + ", version: v1");
			System.out.println(HttpRequest.httpGet("files/"+URLEncoder.encode("c:\\data\\db","UTF-8"),"v1"));
			fail("v1 is obsolute");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testFilesv2() {
		try {
			System.out.println("Endpoint: /files/c:" + ", version: v2");
			System.out.println(HttpRequest.httpGet("files/"+URLEncoder.encode("c:\\data\\db","UTF-8"),"v2"));
		}
		catch (Exception e) {
				fail(e.getMessage() );
		}
	}

}
