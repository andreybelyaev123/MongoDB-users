package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest {
	private static final String servicePart = "/UserService/";
	private static String serverHost = "http://localhost";
	private static short serverPort = 9998;
	
	public static void setHost(String h) {
		serverHost = h;
	}
	public static void setPort(short p) {
		serverPort = p;
	}
	
	private static String getServerURL(String endPoint) {
		return serverHost + ":" + String.valueOf(serverPort) + servicePart +endPoint;
	}
	
	public static String httpGet(String endPoint, String version) throws IOException {
		String line;
		StringBuilder jsonString = new StringBuilder();
	    HttpURLConnection connection = null;
	    try {
	        URL url = new URL(getServerURL(endPoint));	        

	        connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Content-Type", "application/mongo-users-"+version +"; charset=UTF-8");
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	    } catch (IOException e) {
	            throw new IOException(e.getMessage());
	    }
	    finally {
	    	if (connection != null)
		        connection.disconnect();
	    }
	    return jsonString.toString();
	}
	
	public static String httpPost(String endPoint, String payload, String version) throws IOException {
		String line;
		StringBuilder jsonString = new StringBuilder();
	    HttpURLConnection connection = null;
	    try {
	        URL url = new URL(getServerURL(endPoint));	        

	        connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Content-Type", "application/mongo-users-"+version +"; charset=UTF-8");
	        if ( payload != null ) {
	        	OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        	writer.write(payload);
	        	writer.close();
	        }
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	    } catch (IOException e) {
	            throw new IOException(e.getMessage());
	    }
	    finally {
	    	if (connection != null)
		        connection.disconnect();
	    }
	    return jsonString.toString();
	}	
}
