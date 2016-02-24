package tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class testMain {

	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-") || arg.startsWith("/") )
				arg = arg.substring(1);
			if (arg.toLowerCase().equals("help")) {
				printHelp();
				System.exit(0);
			}
				
			int valueIndex = arg.indexOf(":");
			if ( valueIndex < 0 || valueIndex == arg.length() - 1 ) {
				System.out.println("Wrong Parameter. Try with paramenter -help for more information");
				System.exit(1);
			}
				
			String argValue = arg.substring(valueIndex + 1);
			arg = arg.substring(0,valueIndex);
						
			if (arg.equals("host")) {
				if ( argValue.endsWith("/") )
					argValue = argValue.substring(0,argValue.length() - 1);
				HttpRequest.setHost(new String(argValue));
			}
			else if (arg.startsWith("port")) {
				try {
					short port = (short)Integer.parseInt(argValue);
					HttpRequest.setPort(port);
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
		
		Result result = JUnitCore.runClasses(MongoUsersTests.class);
		
		if (!result.getFailures().isEmpty()) {
			System.out.println("List of failures:");
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}
		}
	    System.out.println("Test was successful: " + result.wasSuccessful());
	}
	private static void printHelp() {
		System.out.println("Usage: MongoUsersTest [parameter:value]");
		System.out.println("");		
		System.out.println("List of parameters and default values:");
		System.out.println("host -           server host URL, default \"http://localhost\"");
		System.out.println("port - 	         server port, default 9998");
		System.out.println("");		
	}

}
