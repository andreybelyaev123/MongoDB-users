package org.mongousers;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.sun.net.httpserver.HttpServer;


/**
 *  Process parameters from command line and starts http server
 *       
 */
public class UserServer {
	
	private static String hostURL = "http://localhost/";
	private static short port = 9998;

	public static void main(String[] args) {
		String dbHost = null;
		short dbPort = 0;
						
		for (String arg : args) {
			if (arg.startsWith("-") || arg.startsWith("/") )
				arg = arg.substring(1);
			if (arg.toLowerCase().equals("help")) {
				printHelp();
				System.exit(0);
			}
				
			int valueIndex = arg.indexOf(":");
			if ( valueIndex < 0 || valueIndex == arg.length() - 1 ) {
				System.out.println("Wrong Parameter. Run with paramenter -help for more information");
				System.exit(1);
			}
				
			String argValue = arg.substring(valueIndex + 1);
			arg = arg.substring(0,valueIndex);
						
			if (arg.equals("dbname")) {
				MongoDBConnector.setDBname(new String(argValue));
			}
			else if (arg.equals("dbhost")) {
				dbHost = new String(argValue);
				MongoDBConnector.setDBname(dbHost);
			}
			else if (arg.equals("dbcollection")) {
				UserService.setCollection(new String(argValue));
			} 
			else if (arg.equals("dbport")) {
				try {
					dbPort = (short)Integer.parseInt(argValue);
					MongoDBConnector.setDBport(dbPort);
				}
				catch (Exception e) {
					System.out.println("Value of \"dbport\" must be integer.");
					System.exit(1);
				}
			} 
			else if (arg.equals("host")) {
				hostURL = new String(argValue);
			}
			else if (arg.startsWith("port")) {
				try {
					port = (short)Integer.parseInt(argValue);
				}
				catch (Exception e) {
					System.out.println("Value of \"port\" must be integer.");
					System.exit(1);
				}
			}
			else {
				System.out.println("Wrong parameter: " + arg + ". Run with paramenter -help for more information");
				System.exit(1);
			}
		}
		if (dbHost != null && dbPort == 0) {
			MongoDBConnector.setDBport((short)0);			
		}
		UserServer.RunServer();
	}
	private static void printHelp() {
		System.out.println("Usage: MongoUsers [parameter:value]");
		System.out.println("");		
		System.out.println("List of parameters and default values:");
		System.out.println("host -           host URL, default \"http://localhost/\"");
		System.out.println("port - 	         port, default 9998");
		System.out.println("dbhost -         host of MongoDB, default \"http://localhost/\"");
		System.out.println("dbport -         port of MongoDB, default 27017");
		System.out.println("dbname -         MongoDB database name, default \"test\"");
		System.out.println("dbcollection -	 MongoDB collection, default \"users\"");
		System.out.println("");		
	}
	
	private static void RunServer() {
		try {
			URI baseUri = UriBuilder.fromUri(hostURL).port(port).build();
			ResourceConfig config = new ResourceConfig(UserService.class);
			HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
			
			System.out.format("Http server is started on %s:%d.", hostURL, port);
			System.out.println("\nPress Enter to stop the server.");
		    System.in.read();
		    server.stop(0);
		    System.out.println("Server stopped.");
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
}
