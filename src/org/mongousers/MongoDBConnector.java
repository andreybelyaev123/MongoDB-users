package org.mongousers;

import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.List;
import java.util.ArrayList;

public class MongoDBConnector {
	private static String DBhost = "localhost";
	private static short DBport = 27017;
	private static String DBname = "test";
	
	public static void setDBhost(String host) {
		DBhost = host;
	}
	public static void setDBport(short port) {
		DBport = port;
	}
	public static void setDBname(String name) {
		DBname = name;
	}
	
	MongoClient mongoClient;
	MongoDatabase mongoDb;
	
	String collectionName;
	int	pageSize;
	
	MongoDBConnector(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public boolean connect() {
		try {
			if ( DBport > 0 )
				mongoClient = new MongoClient( DBhost, DBport );
			else
				mongoClient = new MongoClient( DBhost);
			if (mongoClient == null)
				return false;
			mongoDb = mongoClient.getDatabase(DBname);				
			return mongoDb != null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		}
		return false;
	}
	
	public void close() {
		if ( mongoClient != null )
			mongoClient.close();
	}
	
	public String findOne(String filter) {		
		MongoCursor<Document> cursor = null;
		try {
			if ( filter != null) {
				Object o = null;
				o = JSON.parse(filter);
				if (o == null)
					return null;
				cursor = mongoDb.getCollection(collectionName).find((Bson)o).limit(1).iterator();
			}
			else
				cursor = mongoDb.getCollection(collectionName).find().limit(1).iterator();
			if ( !cursor.hasNext() ) 
				return null;
			Document doc = cursor.next();
			if ( doc.containsKey("firstname") )
				return doc.get("firstname").toString();
			return new String("");
		}
		catch (Exception e) {
			return null;
		}
		finally {
			if (cursor != null)
				cursor.close();
		}
	}
	
	public boolean checkCollection() {
		return mongoDb.getCollection(collectionName).find().limit(1).iterator().hasNext();
	}
	
	public List<Document> getDocuments(int pageNumber, int pageSize, String filter, String groupby) 
	{
		List<Bson> pipeLine = new ArrayList<Bson>();
		if (filter != null ) { 
			Object o = JSON.parse(filter);
			if (o != null)
				pipeLine.add(new BasicDBObject("$match", (DBObject)o));
		}
		if (groupby != null) {
			DBObject group = new BasicDBObject("_id", "$" + groupby);
			group.put("users", new BasicDBObject("$push", "$$ROOT"));
			pipeLine.add(new BasicDBObject("$group", group));
		}
		if (pageNumber > 0) 
			pipeLine.add(new BasicDBObject("$skip", pageNumber*pageSize));
		if (pageSize > 0)
			pipeLine.add(new BasicDBObject("$limit", pageSize));
		List<Document> res = new ArrayList<Document>();
		mongoDb.getCollection(collectionName).aggregate(pipeLine).into(res);
		return res;
	}
}
