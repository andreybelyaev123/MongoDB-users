package org.mongousers;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

public final class UserServer {
	
	private static String hostURL = "http://localhost/";
	private static short port = 9998;

	public static void main(String[] args) {
		String dbHost = null;
		short dbPort = 0;
						
		for (String arg : args) {
			System.out.println(arg);
			if (arg.startsWith("/dbname:")) {
				MongoDBConnector.setDBname(arg.substring("/dbname:".length()));
			}
			else if (arg.startsWith("/dbhost:")) {
				dbHost = arg.substring("/dbhost:".length());
				MongoDBConnector.setDBname(dbHost);
			} 
			else if (arg.startsWith("/dbport:")) {
				String envValue = arg.substring("/dbport:".length());
				try {
					dbPort = (short)Integer.parseInt(envValue);
					MongoDBConnector.setDBport(dbPort);
				}
				catch (Exception e) {
					System.out.println("Argument \"dbport\" must be integer.");
					System.exit(1);
				}
			} 
			else if (arg.startsWith("/host:")) {
				hostURL = arg.substring("/host:".length());;
			}
			else if (arg.startsWith("/port:")) {
				String envValue = arg.substring("/port:".length());
				try {
					port = (short)Integer.parseInt(envValue);
				}
				catch (Exception e) {
					System.out.println("Argument \"port\" must be integer.");
					System.exit(1);
				}
			}
		}
		if (dbHost != null && dbPort == 0) {
			MongoDBConnector.setDBport((short)0);			
		}		
		new UserServer().RunServer();
		System.out.println("Server stopped.");
	}
	
	private void RunServer() {
		try {
			URI baseUri = UriBuilder.fromUri(hostURL).port(port).build();
			ResourceConfig config = new ResourceConfig(UserService.class);
			HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
			
			System.out.format("Http server is started on %s:%d.", hostURL, port);
			System.out.println("\nPress Enter to stop the server.");
		    System.in.read();
		    server.stop(0);
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

}


