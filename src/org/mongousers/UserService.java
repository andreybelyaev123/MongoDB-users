package org.mongousers;

import java.util.List;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;
import com.mongodb.util.JSON;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *  Implements endpoints from Path = /UserService 
 */
@Path("/UserService")
public class UserService {	
	static private String collectionName = "users";
		
	static public void setCollection(String name) {
		collectionName = name;
	}
	
	public UserService() {
		
	}
		
	/**
	 *  POST endpoint /login authenticates a user
	 *  Json parameter login should be {"userid":"id","password":"psw"}   
	 *  Function returns {error:"no",text:"Welcome, <firstname>!"}
	 *  or an error in format {error:"yes",text:"error text"}
	 *       
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON,"application/mongo-users-v1+json"})
	public String login(String login){ 
		MongoDBConnector	userClient = null;		
	    try {	    	
	    	JSONObject jsonParam = JsonObject(login);
			if ( jsonParam == null ) {
				return ErrorJSON("Unable to parse request.");
			}
			if ( getParam(jsonParam, "userid") == null)
				return ErrorJSON("User ID is empty.");
			if ( getParam(jsonParam, "password") == null ) 
				return ErrorJSON("Password is empty.");
				
	    	userClient = new MongoDBConnector(collectionName);
		
	    	if ( !userClient.connect() )		
	    		return ErrorJSON("Unable to connect to MangoDb.");
	    	Document doc = userClient.findOne(login); 	    	
	    	if ( doc == null ) 
		    	return ErrorJSON("Incorrect username or password.");
	    	if ( doc.containsKey("firstname") ) {
				String firstName = doc.get("firstname").toString();
	    		if (firstName != null && firstName.length() > 0)
	    			return Json.createObjectBuilder().add("error", "no").add("text", "Welcome, " + firstName + "!").build().toString();
	    	}
    		return Json.createObjectBuilder().add("error", "no").build().toString();
	    }
	    catch (Exception e) {
	    	return ErrorJSON("Unable to login. Error: " + e.getMessage());
	    }
	    finally {
	    	if (userClient != null)
	    		userClient.close();
		}
	}
	/**
	 *  GET endpoint /users?filter=condition&groupby=field&page_number=page&page_size=size 
	 *  condition can be any Json conditon, like {"firstname":"John","profession":"engineer"}
	 *  Parameter groupby can be any field from collection, like address.zipcode
	 *  Parameter size defines max number of documents in reply. If size == 0, then all documents will be returned
	 *  if page is not zero, then first page * size will be skipped   
	 *  Function returns Json string {Result:[{Json documents}]} if number of documents > page_size,
	 *  or {last:"yes",Result:[{Json documents}]}, if it is last page,
	 *  or an error in format {error:"yes",text:"error text"}
	 *       
	 */
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({"application/mongo-users-v1"})
	public String getUsers(@QueryParam("filter") @Encoded String filterPar,
			@QueryParam("groupby") String group,
			@DefaultValue("0") @QueryParam("page_number") int pageNumber,
			@DefaultValue("0") @QueryParam("page_size") int pageSize)
	{		
		
		MongoDBConnector	userClient = null;
	    List<Document> docs = null;    	
	    String filterValue = null;
	    try {
	    	if ( filterPar != null)
	    		filterValue = URLDecoder.decode(filterPar, "UTF-8");
	    	userClient = new MongoDBConnector(collectionName);
		
	    	if ( !userClient.connect() )		
	    		return ErrorJSON("Unable to connect to MangoDb.");
	    	docs = userClient.getDocuments(pageNumber*pageSize,pageSize, filterValue, group);
	    }
	    catch (Exception e) {
	    	return ErrorJSON("Unable to get documents. Error: " + e.getMessage());
	    }
	    finally {
	    	if (userClient != null)
	    		userClient.close();
		}
	    String result = JSON.serialize(docs);
	    if ( docs.size() < pageSize || pageSize == 0 )
	    	return Json.createObjectBuilder().add("last","yes").add("result", result).build().toString(); 
		return Json.createObjectBuilder().add("result", result).build().toString();
	}		
	/**
	 *  GET endpoint /status checks MongoDB connection and checks a collection is not empty   
	 *  Function returns {error:"yes"|"no",text:"error text"}
	 *       
	 */
	@GET
	@Path("/status")
	@Consumes({"application/mongo-users-v1"}) 
	public String getStatus(){
		MongoDBConnector	userClient = null;
	    try {	    	
	    	userClient = new MongoDBConnector(collectionName);
		
	    	if ( !userClient.connect() )		
	    		return ErrorJSON("Unable to connect to MangoDb.");
	    	if ( !userClient.checkCollection() )
	    		return ErrorJSON("Collection \"users\" is empty.");
	    	return Json.createObjectBuilder().add("error", "no").build().toString(); 
	    }
	    catch (Exception e) {
	    	return ErrorJSON("Unable to get documents. Error: " + e.getMessage());
	    }
	    finally {
	    	if (userClient != null)
	    		userClient.close();
		}		
	}
	/**
	 *  GET endpoint /files{dir} returns an array [{filename:"file"|"directory"}] from directory dir
	 *       
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/files/{dir}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({"application/mongo-users-v2"}) 
	public String getFilesv2(@PathParam ("dir") String dir){
		JSONArray res = new JSONArray();
		try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(Paths.get(dir)) ) { 
		    for (java.nio.file.Path path: stream) {
		    	File file = path.toFile();
		    	res.add(Json.createObjectBuilder().add(file.getName(), file.isDirectory() ? "directory" : "file").build());
		    }
		} catch (Exception e) {
			return ErrorJSON("Unable to read file list. Error: " + e.getMessage());
		}
		return res.toString();
	}
	private JSONObject JsonObject(String s) {
		if (s == null)
			return null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject)parser.parse(s);
			return obj;
		}
		catch (Exception e) {
			return null;
		}		
	}	
	private String getParam(JSONObject obj, String parName) {
		try {
			return (String)obj.get(parName);
		}
		catch (Exception e) {
			return null;
		}
	}
	private String ErrorJSON(String error) {
		return Json.createObjectBuilder().add("error", "yes").add("text", error).build().toString();
	}
}