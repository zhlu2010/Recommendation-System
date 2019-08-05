package db.mongo;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBCreation {
	public static void main(String[] args) {
		MongoClient mongoClient = MongoClients.create();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		
		db.getCollection("users").drop();
		db.getCollection("items").drop();
		
		IndexOptions indexOptions = new IndexOptions().unique(true);
		//getCollection will create a new collection if it doesn't exist
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions); //1 means ascending.
		db.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);
		
		db.getCollection("users").insertOne(new Document().append("user_id", "1111")
														.append("password", "3229c1097c00d497a0fd282d586be050")
														.append("first_name", "John").append("last_name", "Smith"));
	}
}
