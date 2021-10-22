package de.hakuyamu.skybee.votesystem.manager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DBManager {

    private MongoClient mongoClient;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> eventCollection;

    public void setup() {
        mongoClient = MongoClients.create(System.getProperty("mongodb.uri"));
        MongoDatabase database = mongoClient.getDatabase("sb_vote_system");
        userCollection = database.getCollection("users");
        eventCollection = database.getCollection("event");
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoCollection<Document> getUserCollection() {
        return userCollection;
    }

    public void updateUser(Bson filter, Bson operation) {
        userCollection.updateOne(filter, operation);
    }

    public List<Document> getUsers(Bson filter) {
        List<Document> result = new ArrayList<>();
        Consumer<Document> consumer = result::add;
        userCollection.find(filter).forEach(consumer);
        return result;
    }

    public MongoCollection<Document> getEventCollection() {
        return eventCollection;
    }

    public void updateEvent(Bson operation) {
        eventCollection.updateOne(Filters.eq("documentType", "event"), operation);
    }

    public Document getEvent() {
        return userCollection.find(Filters.eq("documentType", "event")).first();
    }

    public void shutdown() {
        mongoClient.close();
    }

}
