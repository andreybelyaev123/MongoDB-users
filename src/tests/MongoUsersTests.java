package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import java.net.URLEncoder;

public class MongoUsersTests {
	@Test
	public void testLogin() {
		try {
			System.out.println(HttpRequest.httpPost("login","{\"userid\":\"johngrey\",\"password\":\"johngrey123!\"}","v1+json"));
			System.out.println(HttpRequest.httpPost("login","{\"userid\":\"johngry\",\"password\":\"johngrey123!\"}","v1+json"));			
			System.out.println(HttpRequest.httpPost("login","{\"userid\":\"johngrey\"}","v1+json"));
			System.out.println(HttpRequest.httpPost("login","{\"password\":\"johngrey123!\"}","v1+json"));
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
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"firstname\":\"amy\"}","UTF-8") +	"&groupby=lastname";
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"firstname\":\"amy\"}","UTF-8") + 
					"&groupby=address.zipcode&page_size=2&page_number=0";
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"lastname\":\"green\"}","UTF-8") + 
					"&groupby=profession&page_size=2&page_number=0";			
			System.out.println(HttpRequest.httpGet(endPoint,"v1"));
			endPoint = "users?filter=" + URLEncoder.encode("{\"lastname\":\"green\"}","UTF-8") + 
					"&groupby=profession&page_size=2&page_number=1";
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
			System.out.println(HttpRequest.httpGet("status","v1"));
		}
		catch (Exception e) {
				fail(e.getMessage() );
		}
	}
	@Test
	public void testFiles() {
		try {
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
			System.out.println(HttpRequest.httpGet("files/"+URLEncoder.encode("c:\\data\\db","UTF-8"),"v2"));
		}
		catch (Exception e) {
				fail(e.getMessage() );
		}
	}

}
